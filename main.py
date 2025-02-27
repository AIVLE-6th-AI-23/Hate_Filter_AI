import asyncio
import logging
import os
import tempfile
from collections import OrderedDict
from contextlib import asynccontextmanager
from pathlib import Path
from typing import List

from fastapi import (
    BackgroundTasks,
    Depends,
    FastAPI,
    File,
    HTTPException,
    UploadFile,
    status,
)
from pydantic import BaseModel, Field

from utils.constants import MAX_UPLOAD_BYTES, REQUEST_DEDUP_CACHE_SIZE
from utils.file_download import download_file_from_url
from utils.http_client import close_http_client
from utils.security import require_api_key
from utils.status import exit_status, notify_relay_ready, update_spring_status
from utils.type import (
    AnalysisCategoryResultRequestDto,
    AnalysisStartRequestDTO,
    ContentAnalysisRequestDto,
)

logger = logging.getLogger(__name__)

_accepted_request_ids = OrderedDict()
_accepted_request_ids_lock = asyncio.Lock()


@asynccontextmanager
async def lifespan(_: FastAPI):
    try:
        yield
    finally:
        await close_http_client()


app = FastAPI(title="Hate Filter AI Server", lifespan=lifespan)


@app.get("/health", tags=["Health"])
async def health():
    return {"status": "ok"}


@app.post(
    "/analyze/start",
    response_model=bool,
    status_code=status.HTTP_202_ACCEPTED,
    dependencies=[Depends(require_api_key)],
)
async def start(
    request: AnalysisStartRequestDTO,
    background_tasks: BackgroundTasks,
) -> bool:
    request_id = str(request.requestId)
    async with _accepted_request_ids_lock:
        if request_id in _accepted_request_ids:
            logger.info("Duplicate analysis request ignored: request_id=%s", request_id)
            return True

        _accepted_request_ids[request_id] = None
        if len(_accepted_request_ids) > REQUEST_DEDUP_CACHE_SIZE:
            _accepted_request_ids.popitem(last=False)

    background_tasks.add_task(analyze, request)
    logger.info("Analysis request accepted: request_id=%s", request.requestId)
    return True


async def analyze(request: AnalysisStartRequestDTO) -> None:
    file_path = None

    try:
        await update_spring_status(
            request.boardId,
            request.postId,
            "Start Analysis",
            0,
        )
        file_path = await download_file_from_url(str(request.thumbnail))
        file_type = categorize_content_file(file_path)
        result = await run_content_analysis(
            file_type,
            file_path,
            request.boardId,
            request.postId,
        )
        result_summary = ContentAnalysisRequestDto(
            contentType=file_type,
            analysisDetail=build_analysis_summary(result),
        )
        await exit_status(
            request.boardId,
            request.postId,
            request.employeeId,
            result,
            result_summary,
        )
    except Exception:
        logger.exception("Analysis failed: request_id=%s", request.requestId)
        try:
            await update_spring_status(
                request.boardId,
                request.postId,
                "FAILED: AI analysis failed",
                0,
            )
        except Exception:
            logger.exception(
                "Failed to report analysis failure to Spring: request_id=%s",
                request.requestId,
            )
    finally:
        if file_path is not None:
            try:
                os.remove(file_path)
            except FileNotFoundError:
                pass
            except OSError:
                logger.exception(
                    "Failed to remove downloaded file: request_id=%s",
                    request.requestId,
                )

        try:
            await notify_relay_ready(
                str(request.requestId),
                request.boardId,
                request.postId,
            )
        except Exception:
            logger.exception(
                "Failed to notify Relay that analysis finished: request_id=%s",
                request.requestId,
            )


def categorize_content_file(file_path: str) -> str:
    from utils.mime_detector import categorize_file

    return categorize_file(file_path)


async def run_content_analysis(
    file_type: str,
    file_path: str,
    board_id: int,
    post_id: int,
):
    if file_type == "text":
        from services.text_analysis import analyzeText

        return await analyzeText(file_path, board_id, post_id)
    if file_type == "image":
        from services.image_analysis import analyzeImage

        return await analyzeImage(file_path, board_id, post_id)
    if file_type == "video":
        from services.video_analysis import analyzeVideo

        return await analyzeVideo(file_path, board_id, post_id)
    raise ValueError(f"Unsupported analysis type: {file_type}")


def build_analysis_summary(result: List[AnalysisCategoryResultRequestDto]) -> str:
    if not result:
        return (
            "분석 결과 해당 콘텐츠에서 혐오 표현이 감지되지 않았습니다. "
            "AI 기반 자동 분석 결과이므로 콘텐츠 정책 및 내부 검수 기준에 따라 "
            "추가 확인이 필요할 수 있습니다."
        )

    category_counts = {}
    for detection in result:
        category = detection.categoryName
        category_counts[category] = category_counts.get(category, 0) + 1

    detected_summary = ", ".join(
        f"{count}건의 {category}" for category, count in category_counts.items()
    )
    return (
        f"분석 결과 해당 콘텐츠에서 총 {len(result)}건의 혐오 표현이 감지되었습니다. "
        f"감지 유형: {detected_summary}. "
        "AI 기반 자동 분석 결과이므로 콘텐츠 정책 및 내부 검수 기준에 따라 "
        "추가 확인이 필요할 수 있습니다."
    )


class AnalysisRequest(BaseModel):
    text: str = Field(min_length=1, max_length=50_000)


class AnalysisResponse(BaseModel):
    result: List[AnalysisCategoryResultRequestDto]


@app.post(
    "/detect/text",
    response_model=AnalysisResponse,
    dependencies=[Depends(require_api_key)],
)
async def detect_text(request: AnalysisRequest):
    from utils.hate_expression import detect_hate_expression

    try:
        result = await asyncio.to_thread(detect_hate_expression, request.text)
        return AnalysisResponse(result=result)
    except Exception as exc:
        logger.exception("Direct text analysis failed")
        raise HTTPException(status_code=500, detail="Text analysis failed") from exc


@app.post(
    "/detect/image",
    response_model=AnalysisResponse,
    dependencies=[Depends(require_api_key)],
)
async def detect_image(file: UploadFile = File(...)):
    import cv2
    import numpy as np

    from utils.hate_gesture import detect_gestures

    image_bytes = await read_limited_upload(file)
    image = cv2.imdecode(np.frombuffer(image_bytes, np.uint8), cv2.IMREAD_COLOR)
    if image is None:
        raise HTTPException(status_code=400, detail="Invalid image file")

    try:
        result = await asyncio.to_thread(detect_gestures, image)
        return AnalysisResponse(result=result)
    except Exception as exc:
        logger.exception("Direct image analysis failed")
        raise HTTPException(status_code=500, detail="Image analysis failed") from exc


@app.post(
    "/detect/video",
    response_model=AnalysisResponse,
    dependencies=[Depends(require_api_key)],
)
async def detect_video(file: UploadFile = File(...)):
    import cv2

    from utils.hate_videoframes import detect_hate_videoframes

    video_bytes = await read_limited_upload(file)
    temporary_path = None
    capture = None

    try:
        with tempfile.NamedTemporaryFile(delete=False, suffix=".mp4") as temporary:
            temporary.write(video_bytes)
            temporary_path = temporary.name

        capture = cv2.VideoCapture(temporary_path)
        if not capture.isOpened():
            raise HTTPException(status_code=400, detail="Invalid video file")

        result = await detect_hate_videoframes(0, 0, capture, False)
        return AnalysisResponse(result=result)
    except HTTPException:
        raise
    except Exception as exc:
        logger.exception("Direct video analysis failed")
        raise HTTPException(status_code=500, detail="Video analysis failed") from exc
    finally:
        if capture is not None:
            capture.release()
        if temporary_path is not None:
            Path(temporary_path).unlink(missing_ok=True)


async def read_limited_upload(file: UploadFile) -> bytes:
    chunks = []
    total_bytes = 0

    while chunk := await file.read(1024 * 1024):
        total_bytes += len(chunk)
        if total_bytes > MAX_UPLOAD_BYTES:
            raise HTTPException(
                status_code=status.HTTP_413_CONTENT_TOO_LARGE,
                detail="Upload exceeds the configured size limit",
            )
        chunks.append(chunk)

    return b"".join(chunks)
