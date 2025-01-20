from .base_imports import re, List, Union

class TextPreprocessor:
    def __init__(self):
        # self.max_length = 512  # 텍스트 최대 길이

    def process(self, text: Union[str, List[str]]) -> Union[str, List[str]]:
        if isinstance(text, list):
            return [self._preprocess_text(t) for t in text]
        return self._preprocess_text(text)

    def _preprocess_text(self, text: str) -> str:
        # 소문자 변환
        text = text.lower()
        
        # 특수문자 제거
        text = re.sub(r'[^\w\s]', '', text)
        
        # 공백 정규화
        text = ' '.join(text.split())
        
        # # 길이 제한
        # text = text[:self.max_length]
        
        return text