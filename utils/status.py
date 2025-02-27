from typing import List

from .constants import BASE_API_URL, RELAY_SERVER_URL, SERVER_API_KEY
from .http_client import request_with_retry
from .type import AnalysisCategoryResultRequestDto, ContentAnalysisRequestDto

HEADERS = {"X-API-KEY": SERVER_API_KEY}


async def update_spring_status(
    board_id: int,
    post_id: int,
    status: str,
    progress: int,
) -> None:
    await request_with_retry(
        "PATCH",
        f"{BASE_API_URL}/api/{board_id}/posts/{post_id}/status",
        headers=HEADERS,
        params={"status": status, "progress": progress},
    )


async def exit_status(
    board_id: int,
    post_id: int,
    employee_id: str,
    result: List[AnalysisCategoryResultRequestDto],
    result_summary: ContentAnalysisRequestDto,
) -> None:
    analysis_payload = {
        "contentAnalysisRequestDto": result_summary.model_dump(),
        "analysisCategoryResultRequestDto": [item.model_dump() for item in result],
    }
    await request_with_retry(
        "POST",
        f"{BASE_API_URL}/api/{post_id}/content-analysis/create",
        headers=HEADERS,
        json=analysis_payload,
        retry=False,
    )

    await update_spring_status(board_id, post_id, "COMPLETED", 100)

    notification_payload = {
        "employeeId": employee_id,
        "postId": post_id,
        "boardId": board_id,
        "resultSummary": result_summary.analysisDetail,
    }
    await request_with_retry(
        "POST",
        f"{BASE_API_URL}/api/{post_id}/content-analysis/notifications",
        headers=HEADERS,
        json=notification_payload,
        retry=False,
    )


async def notify_relay_ready(request_id: str, board_id: int, post_id: int) -> None:
    await request_with_retry(
        "POST",
        f"{RELAY_SERVER_URL}/status/ok",
        headers=HEADERS,
        json={
            "requestId": request_id,
            "boardId": board_id,
            "postId": post_id,
        },
    )
