import cv2
import numpy as np

def extract_frames(video_path, frame_interval=30):
    """
    비디오에서 일정 간격으로 프레임 추출
    """
    frames = []
    video = cv2.VideoCapture(video_path)
    
    frame_count = 0
    while True:
        success, frame = video.read()
        if not success:
            break
            
        if frame_count % frame_interval == 0:
            # BGR to RGB 변환
            frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            frames.append(frame_rgb)
            
        frame_count += 1
        
    video.release()
    return frames
