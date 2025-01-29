from fastapi import FastAPI
from core.config import setup_cors
from api.endpoints import image

app = FastAPI()
setup_cors(app)
app.include_router(image.router, prefix="/api")
