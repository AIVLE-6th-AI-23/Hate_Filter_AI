import asyncio
import logging
from typing import Any, Dict, Optional

import httpx

from .constants import (
    HTTP_MAX_RETRIES,
    HTTP_RETRY_BASE_DELAY_SECONDS,
    HTTP_TIMEOUT_SECONDS,
)

logger = logging.getLogger(__name__)

_http_client: Optional[httpx.AsyncClient] = None


class UpstreamRequestError(RuntimeError):
    pass


def get_http_client() -> httpx.AsyncClient:
    global _http_client
    if _http_client is None:
        _http_client = httpx.AsyncClient(
            timeout=HTTP_TIMEOUT_SECONDS,
            follow_redirects=False,
        )
    return _http_client


async def close_http_client() -> None:
    global _http_client
    if _http_client is not None:
        await _http_client.aclose()
        _http_client = None


async def request_with_retry(
    method: str,
    url: str,
    *,
    headers: Optional[Dict[str, str]] = None,
    json: Optional[Dict[str, Any]] = None,
    params: Optional[Dict[str, Any]] = None,
    retry: bool = True,
) -> httpx.Response:
    client = get_http_client()
    max_attempts = HTTP_MAX_RETRIES if retry else 1

    for attempt in range(1, max_attempts + 1):
        try:
            response = await client.request(
                method,
                url,
                headers=headers,
                json=json,
                params=params,
            )
        except httpx.RequestError as exc:
            logger.warning(
                "Upstream request failed: method=%s attempt=%s/%s error=%s",
                method,
                attempt,
                max_attempts,
                type(exc).__name__,
            )
            if attempt == max_attempts:
                raise UpstreamRequestError("Upstream server did not respond") from exc
        else:
            if response.is_success:
                return response

            retryable = (
                response.status_code in {408, 429} or response.status_code >= 500
            )
            logger.warning(
                "Upstream returned an error: method=%s status=%s attempt=%s/%s",
                method,
                response.status_code,
                attempt,
                max_attempts,
            )
            if not retryable or attempt == max_attempts:
                raise UpstreamRequestError(
                    f"Upstream server returned HTTP {response.status_code}"
                )

        await asyncio.sleep(HTTP_RETRY_BASE_DELAY_SECONDS * (2 ** (attempt - 1)))

    raise UpstreamRequestError("Upstream request failed")
