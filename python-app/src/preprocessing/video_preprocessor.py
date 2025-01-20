import cv2
import numpy as np
from typing import Union, List

class VideoPreprocessor:
    def __init__(self):
        self.frame_count = 32  # 분석할 프레임 수
        self.frame_size = (224, 224)  # 프레임 크기
        self.image_preprocessor = ImagePreprocessor()

    def process(self, video_path: Union[str, List[str]]) -> Union[np.ndarray, List[np.ndarray]]:
        if isinstance(video_path, list):
            return [self._preprocess_video(path) for path in video_path]
        return self._preprocess_video(video_path)

    def _preprocess_video(self, video_path: str) -> np.ndarray:
        frames = []
        cap = cv2.VideoCapture(video_path)
        
        # 동영상에서 프레임 추출
        while len(frames) < self.frame_count:
            ret, frame = cap.read()
            if not ret:
                break
            # 이미지 전처리 적용
            processed_frame = self.image_preprocessor._preprocess_image(frame)
            frames.append(processed_frame)
        
        cap.release()
        
        # 프레임 수 맞추기
        if len(frames) < self.frame_count:
            # 부족한 프레임은 마지막 프레임으로 채우기
            last_frame = frames[-1] if frames else np.zeros(self.frame_size + (3,))
            frames.extend([last_frame] * (self.frame_count - len(frames)))
        
        return np.array(frames)