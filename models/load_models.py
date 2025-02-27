from functools import lru_cache


@lru_cache(maxsize=1)
def load_kr_model(model_path="kr_text_detector"):
    from transformers import (
        AutoModelForSequenceClassification,
        AutoTokenizer,
        TextClassificationPipeline,
    )

    tokenizer = AutoTokenizer.from_pretrained(model_path)
    model = AutoModelForSequenceClassification.from_pretrained(
        model_path, use_safetensors=True
    )
    classification = TextClassificationPipeline(
        model=model, tokenizer=tokenizer, device=-1, top_k=3
    )
    return classification


@lru_cache(maxsize=1)
def load_gesture_model():
    from ultralytics import YOLO

    return YOLO("YOLOv10x_gestures.pt")
