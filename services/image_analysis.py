import asyncio
from pathlib import Path

import cv2

from utils.hate_expression import detect_hate_expression
from utils.hate_gesture import detect_gestures
from utils.ocr import try_all_readers
from utils.status import update_spring_status


async def analyzeImage(file_path: str, boardId: int, postId: int):
    await update_spring_status(boardId, postId, "Start Image Analysis", 10)

    if not Path(file_path).exists():
        raise FileNotFoundError(f"파일을 찾을 수 없음: {file_path}")

    image = await asyncio.to_thread(cv2.imread, file_path)
    if image is None:
        raise ValueError("이미지를 로드할 수 없음")

    await update_spring_status(
        boardId,
        postId,
        "Processing OCR & Text Analysis",
        30,
    )
    ocr_result = await asyncio.to_thread(try_all_readers, image)

    text_detection_result = []
    text_content = ocr_result["text"]
    if text_content.strip():
        text_detection_result = await asyncio.to_thread(
            detect_hate_expression,
            text_content,
        )

    await update_spring_status(boardId, postId, "Processing Image Analysis", 60)
    gesture_detection_result = await asyncio.to_thread(detect_gestures, image)

    await update_spring_status(boardId, postId, "Merging Detection Results", 90)
    return text_detection_result + gesture_detection_result
