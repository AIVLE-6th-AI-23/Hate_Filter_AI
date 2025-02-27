import os
import tempfile
import unittest

os.environ["OPENAI_API_KEY"] = "test-openai-key"
os.environ["BASE_API_URL"] = "https://spring.example.com"
os.environ["RELAY_SERVER_URL"] = "https://relay.example.com"
os.environ["SERVER_API_KEY"] = "test-server-key"
os.environ["ALLOWED_DOWNLOAD_HOSTS"] = "storage.example.com"

import httpx

from utils import file_download, http_client


class FileDownloadTest(unittest.IsolatedAsyncioTestCase):
    def test_rejects_non_https_and_unapproved_hosts(self):
        with self.assertRaises(file_download.DownloadValidationError):
            file_download.validate_download_url("http://storage.example.com/file.txt")

        with self.assertRaises(file_download.DownloadValidationError):
            file_download.validate_download_url("https://internal.example.com/file.txt")

        with self.assertRaises(file_download.DownloadValidationError):
            file_download.validate_download_url(
                "https://storage.example.com:8443/file.txt"
            )

    async def test_enforces_streamed_download_size_limit(self):
        def handle(request: httpx.Request) -> httpx.Response:
            return httpx.Response(200, request=request, content=b"123456")

        original_client = http_client._http_client
        original_directory = file_download.SAVE_DIRECTORY
        original_limit = file_download.MAX_DOWNLOAD_BYTES

        with tempfile.TemporaryDirectory() as directory:
            http_client._http_client = httpx.AsyncClient(
                transport=httpx.MockTransport(handle)
            )
            file_download.SAVE_DIRECTORY = directory
            file_download.MAX_DOWNLOAD_BYTES = 5

            try:
                with self.assertRaises(file_download.DownloadValidationError):
                    await file_download.download_file_from_url(
                        "https://storage.example.com/file.txt"
                    )
                self.assertEqual([], os.listdir(directory))
            finally:
                await http_client._http_client.aclose()
                http_client._http_client = original_client
                file_download.SAVE_DIRECTORY = original_directory
                file_download.MAX_DOWNLOAD_BYTES = original_limit


if __name__ == "__main__":
    unittest.main()
