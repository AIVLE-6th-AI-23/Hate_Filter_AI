from .base_imports import np, cv2, easyocr, Union, List, Dict, Any

class ImagePreprocessor:
    def __init__(self):
        self.target_size = (224, 224)
        self.mean = np.array([0.485, 0.456, 0.406])
        self.std = np.array([0.229, 0.224, 0.225])
        # OCR reader 초기화
        self.ocr_reader = easyocr.Reader(['ko', 'en'])

    def process(self, image: Union[np.ndarray, List[np.ndarray]]) -> Dict[str, Any]:
        """
        이미지 전처리 및 OCR 처리를 수행하는 통합 메서드
        """
        if isinstance(image, list):
            return [self._process_single_image(img) for img in image]
        return self._process_single_image(image)

    def _process_single_image(self, image: np.ndarray) -> Dict[str, Any]:
        try:
            # 기본 이미지 전처리
            processed_image = self._preprocess_image(image)
            
            # OCR 처리
            ocr_results = self._extract_text(image)
            
            return {
                'processed_image': processed_image,
                'ocr_text': ocr_results,
                'status': 'success'
            }
        except Exception as e:
            return {
                'status': 'error',
                'message': str(e)
            }

    def _preprocess_image(self, image: np.ndarray) -> np.ndarray:
        """기존의 이미지 전처리 로직"""
        image = cv2.resize(image, self.target_size)
        image = image.astype(np.float32) / 255.0
        image = (image - self.mean) / self.std
        return image

    def _extract_text(self, image: np.ndarray) -> List[str]:
        """OCR 텍스트 추출"""
        try:
            results = self.ocr_reader.readtext(image, detail=0)
            return results
        except Exception as e:
            print(f"OCR 처리 중 오류 발생: {str(e)}")
            return []

# # 사용 예시
# if __name__ == "__main__":
#     # 전처리기 초기화
#     preprocessor = ImagePreprocessor()
    
#     # 이미지 읽기
#     image = cv2.imread("test_image.jpg")
    
#     # 처리 실행
#     result = preprocessor.process(image)
    
#     if result['status'] == 'success':
#         print("전처리된 이미지 shape:", result['processed_image'].shape)
#         print("\n추출된 텍스트:")
#         for idx, text in enumerate(result['ocr_text'], 1):
#             print(f"{idx}. {text}")
#     else:
#         print(f"오류 발생: {result['message']}")
