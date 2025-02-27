from pathlib import Path

import cv2

from utils.hate_videoframes import detect_hate_videoframes
from utils.status import update_spring_status


async def analyzeVideo(file_path: str, boardId: int, postId: int):
    await update_spring_status(boardId, postId, "Start Video Analysis", 10)

    if not Path(file_path).exists():
        raise FileNotFoundError(f"파일을 찾을 수 없음: {file_path}")

    capture = cv2.VideoCapture(file_path)
    if not capture.isOpened():
        capture.release()
        raise ValueError("비디오 파일을 열 수 없음")

    detection_result = await detect_hate_videoframes(
        boardId,
        postId,
        capture,
    )
    await update_spring_status(boardId, postId, "Merging Detection Results", 90)
    return detection_result
