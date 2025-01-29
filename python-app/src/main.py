from fastapi import FastAPI
from app.core.config import setup_cors
from app.api.endpoints import image

app = FastAPI()
setup_cors(app)
app.include_router(image.router, prefix="/api")
