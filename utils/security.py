import secrets
from typing import Optional

from fastapi import Header, HTTPException, status

from .constants import SERVER_API_KEY


async def require_api_key(
    x_api_key: Optional[str] = Header(default=None, alias="X-API-KEY"),
) -> None:
    if x_api_key is None or not secrets.compare_digest(x_api_key, SERVER_API_KEY):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid API key",
        )
