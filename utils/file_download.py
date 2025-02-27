import os
import uuid
from pathlib import Path
from urllib.parse import unquote, urlsplit

import httpx

from .constants import ALLOWED_DOWNLOAD_HOSTS, MAX_DOWNLOAD_BYTES, SAVE_DIRECTORY
from .http_client import get_http_client


class DownloadValidationError(ValueError):
    pass


def validate_download_url(url: str) -> None:
    parsed = urlsplit(url)
    hostname = (parsed.hostname or "").lower()
    try:
        port = parsed.port
    except ValueError as exc:
        raise DownloadValidationError("Download URL contains an invalid port") from exc

    if parsed.scheme != "https":
        raise DownloadValidationError("Download URL must use HTTPS")
    if parsed.username is not None or parsed.password is not None:
        raise DownloadValidationError("Download URL must not contain credentials")
    if port not in {None, 443}:
        raise DownloadValidationError("Download URL must use the default HTTPS port")
    if hostname not in ALLOWED_DOWNLOAD_HOSTS:
        raise DownloadValidationError("Download host is not allowed")


def _safe_suffix(url: str) -> str:
    suffix = Path(unquote(urlsplit(url).path)).suffix.lower()
    if 1 < len(suffix) <= 10 and suffix[1:].isalnum():
        return suffix
    return ".bin"


async def download_file_from_url(url: str) -> str:
    validate_download_url(url)

    download_directory = Path(SAVE_DIRECTORY)
    download_directory.mkdir(parents=True, exist_ok=True)
    file_path = download_directory / f"{uuid.uuid4()}{_safe_suffix(url)}"
    downloaded_bytes = 0

    try:
        client = get_http_client()
        async with client.stream("GET", url) as response:
            if not response.is_success:
                raise httpx.HTTPStatusError(
                    f"Download server returned HTTP {response.status_code}",
                    request=response.request,
                    response=response,
                )

            content_length = response.headers.get("Content-Length")
            if content_length is not None and int(content_length) > MAX_DOWNLOAD_BYTES:
                raise DownloadValidationError(
                    "Download exceeds the configured size limit"
                )

            with file_path.open("wb") as output:
                async for chunk in response.aiter_bytes():
                    downloaded_bytes += len(chunk)
                    if downloaded_bytes > MAX_DOWNLOAD_BYTES:
                        raise DownloadValidationError(
                            "Download exceeds the configured size limit"
                        )
                    output.write(chunk)
    except Exception:
        try:
            os.remove(file_path)
        except FileNotFoundError:
            pass
        raise

    return str(file_path)
