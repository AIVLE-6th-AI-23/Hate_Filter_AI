import cv2
import numpy as np
from typing import Union, List

class ImagePreprocessor:
    def __init__(self):
        self.target_size = (224, 224)  # 모델에 맞는 크기
        self.mean = np.array([0.485, 0.456, 0.406])
        self.std = np.array([0.229, 0.224, 0.225])

    def process(self, image: Union[np.ndarray, List[np.ndarray]]) -> Union[np.ndarray, List[np.ndarray]]:
        if isinstance(image, list):
            return [self._preprocess_image(img) for img in image]
        return self._preprocess_image(image)

    def _preprocess_image(self, image: np.ndarray) -> np.ndarray:
        # 이미지 크기 조정
        image = cv2.resize(image, self.target_size)
        # 정규화
        image = image.astype(np.float32) / 255.0
        image = (image - self.mean) / self.std
        return image