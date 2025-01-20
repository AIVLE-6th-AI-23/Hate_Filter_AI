import re
from typing import List, Union

class TextPreprocessor:
    def __init__(self):
        self.max_length = 512  # 또는 모델에 맞는 길이

    def process(self, text: Union[str, List[str]]) -> Union[str, List[str]]:
        if isinstance(text, list):
            return [self._preprocess_text(t) for t in text]
        return self._preprocess_text(text)

    def _preprocess_text(self, text: str) -> str:
        # 텍스트 정제
        text = text.lower()
        text = re.sub(r'[^\w\s]', '', text)
        text = ' '.join(text.split())
        # 텍스트 길이 제한
        return text[:self.max_length]