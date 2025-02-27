from typing import List

from models import load_gesture_model

from .constants import DETECTABLE_HATE_GESTURES, GESTURE_TO_ISO
from .type import AnalysisCategoryResultRequestDto

gesture_model = None


def get_gesture_model():
    global gesture_model
    if gesture_model is None:
        gesture_model = load_gesture_model()
    return gesture_model


def detect_gestures(image) -> List[AnalysisCategoryResultRequestDto]:
    gesture_model = get_gesture_model()
    gesture_results = gesture_model.predict(image)
    detections: List[AnalysisCategoryResultRequestDto] = []
    for result in gesture_results:
        for box in result.boxes:
            bbox = box.xyxy[0].tolist()
            category = result.names[int(box.cls)].strip("'")

            if category in DETECTABLE_HATE_GESTURES:
                detection = AnalysisCategoryResultRequestDto(
                    categoryName=category,
                    categoryScore=float(box.conf),
                    detectionMetadata={
                        "bbox": bbox,
                        "countries": GESTURE_TO_ISO[category],
                    },
                )
                detections.append(detection.model_copy())

    return detections
