from fastapi import APIRouter, UploadFile, File
from services.image_service import ImageService
from services.video_service import VideoService

router = APIRouter()
image_service = ImageService()
video_service = VideoService()

@router.post("/analyze/image")
async def analyze_image(file: UploadFile = File(...)):
    if not file.content_type.startswith('image/'):
        return {"error": "File must be an image"}
    return await image_service.analyze(file)

@router.post("/analyze/video")
async def analyze_video(file: UploadFile = File(...)):
    if not file.content_type.startswith('video/'):
        return {"error": "File must be a video"}
    return await video_service.analyze(file)
