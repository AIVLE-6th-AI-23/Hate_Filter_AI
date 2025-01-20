from .base_imports import np, cv2, Union, List, Dict
from .image_preprocessor import ImagePreprocessor

class VideoPreprocessor:
    def __init__(self):
        self.frame_count = 32
        self.frame_size = (224, 224)
        self.image_preprocessor = ImagePreprocessor()

    def process(self, video_path: Union[str, List[str]]) -> dict:
        if isinstance(video_path, list):
            return [self._process_video(path) for path in video_path]
        return self._process_video(video_path)

    def _process_video(self, video_path: str) -> dict:
        try:
            frames = []
            ocr_results = []
            cap = cv2.VideoCapture(video_path)
            
            while len(frames) < self.frame_count:
                ret, frame = cap.read()
                if not ret:
                    break
                
                # 이미지 전처리 및 OCR 처리
                processed_result = self.image_preprocessor.process(frame)
                
                if processed_result['status'] == 'success':
                    frames.append(processed_result['processed_image'])
                    if processed_result['ocr_text']:
                        ocr_results.extend(processed_result['ocr_text'])
            
            cap.release()
            
            # 프레임 수 맞추기
            if len(frames) < self.frame_count:
                last_frame = frames[-1] if frames else np.zeros(self.frame_size + (3,))
                frames.extend([last_frame] * (self.frame_count - len(frames)))
            
            return {
                'status': 'success',
                'processed_frames': np.array(frames),
                'ocr_results': ocr_results,
                'frame_count': len(frames)
            }
            
        except Exception as e:
            return {
                'status': 'error',
                'message': str(e)
            }
