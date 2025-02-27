import asyncio
from pathlib import Path

from utils.hate_expression import detect_hate_expression
from utils.status import update_spring_status


async def analyzeText(file_path: str, boardId: int, postId: int):
    await update_spring_status(boardId, postId, "Start Text Analysis", 10)

    path = Path(file_path)
    if not path.exists():
        raise FileNotFoundError(f"파일을 찾을 수 없음: {file_path}")

    text_content = await asyncio.to_thread(path.read_text, encoding="utf-8")

    await update_spring_status(boardId, postId, "Processing Text Analysis", 30)
    return await asyncio.to_thread(detect_hate_expression, text_content)
