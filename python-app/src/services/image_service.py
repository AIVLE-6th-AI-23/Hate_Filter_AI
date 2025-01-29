from PIL import Image
import io
from models.ai_models import text_detector, hate_speech_classifier

class ImageService:
    async def analyze(self, file):
        image_bytes = await file.read()
        image = Image.open(io.BytesIO(image_bytes))
        
        # 이미지에서 텍스트 추출
        texts = text_detector.extract_text(image)
        
        # 각 텍스트에 대한 혐오 표현 분석
        results = []
        for text in texts:
            analysis = hate_speech_classifier.analyze(text)
            results.append({
                "text": text,
                "analysis": analysis
            })
            
        return {
            "type": "image",
            "results": results
        }
