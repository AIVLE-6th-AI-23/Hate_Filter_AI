from transformers import pipeline
import pytesseract
from PIL import Image

class TextDetector:
    def extract_text(self, image):
        text = pytesseract.image_to_string(image, lang='kor+eng')
        # 텍스트를 문장 단위로 분리
        sentences = [s.strip() for s in text.split('\n') if s.strip()]
        return sentences

class HateSpeechClassifier:
    def __init__(self):
        self.model = pipeline(
            "text-classification",
            model="jhgan/ko-hate-speech",
            return_all_scores=True
        )
    
    def analyze(self, text):
        result = self.model(text)
        return {
            "scores": result[0],
            "is_harmful": result[0][0]['score'] > 0.5
        }

text_detector = TextDetector()
hate_speech_classifier = HateSpeechClassifier()