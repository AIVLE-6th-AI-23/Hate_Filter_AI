import cv2
import numpy as np
from utils.media_utils import extract_frames
from models.ai_models import text_detector, hate_speech_classifier

class VideoService:
    async def analyze(self, file):
        # 비디오 파일을 임시 저장
        video_bytes = await file.read()
        temp_path = f"temp_{file.filename}"
        with open(temp_path, "wb") as f:
            f.write(video_bytes)
            
        # 프레임 추출
        frames = extract_frames(temp_path)
        
        # 각 프레임 분석
        results = []
        for frame_idx, frame in enumerate(frames):
            texts = text_detector.extract_text(frame)
            frame_results = []
            
            for text in texts:
                analysis = hate_speech_classifier.analyze(text)
                frame_results.append({
                    "text": text,
                    "analysis": analysis
                })
                
            results.append({
                "frame": frame_idx,
                "results": frame_results
            })
            
        return {
            "type": "video",
            "total_frames": len(frames),
            "results": results
        }
