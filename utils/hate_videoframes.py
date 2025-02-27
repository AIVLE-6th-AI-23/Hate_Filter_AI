import asyncio
from typing import List

import cv2

from .constants import FRAME_THRESHOLD
from .hate_expression import detect_hate_expression
from .hate_gesture import detect_gestures
from .ocr import try_all_readers
from .status import update_spring_status
from .type import AnalysisCategoryResultRequestDto


async def detect_hate_videoframes(
    boardId, postId, cap, send=True
) -> List[AnalysisCategoryResultRequestDto]:
    try:
        total_frames = max(1, int(cap.get(cv2.CAP_PROP_FRAME_COUNT)))
        fps = cap.get(cv2.CAP_PROP_FPS)
        frame_results: List[AnalysisCategoryResultRequestDto] = []
        frame_count = 0

        while cap.isOpened():
            ret, frame = cap.read()
            if not ret:
                break

            progress = min(90, round(10 + (frame_count / total_frames * 80)))
            if send:
                await update_spring_status(
                    boardId, postId, "Processing Video", progress
                )

            timestamp = frame_count / fps if fps > 0 else 0

            text_result = await asyncio.to_thread(try_all_readers, frame)
            text_detections = (
                await asyncio.to_thread(detect_hate_expression, text_result["text"])
                if text_result["text"].strip()
                else []
            )

            gesture_detections = await asyncio.to_thread(detect_gestures, frame)

            for i, detection in enumerate(text_detections):
                detection.detectionMetadata["frame"] = frame_count
                detection.detectionMetadata["timestamp"] = round(timestamp, 2)

                text_detections[i] = detection.model_copy(
                    update={"detectionMetadata": detection.detectionMetadata}
                )

            for i, detection in enumerate(gesture_detections):
                detection.detectionMetadata["frame"] = frame_count
                detection.detectionMetadata["timestamp"] = round(timestamp, 2)

                gesture_detections[i] = detection.model_copy(
                    update={"detectionMetadata": detection.detectionMetadata}
                )

            frame_results += text_detections + gesture_detections

            frame_count += FRAME_THRESHOLD
            cap.set(cv2.CAP_PROP_POS_FRAMES, frame_count)
        if send:
            await update_spring_status(boardId, postId, "Video Analysis Completed", 90)
        return frame_results

    finally:
        cap.release()
