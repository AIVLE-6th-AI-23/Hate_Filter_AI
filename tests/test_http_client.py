import os
import unittest

os.environ["OPENAI_API_KEY"] = "test-openai-key"
os.environ["BASE_API_URL"] = "https://spring.example.com"
os.environ["RELAY_SERVER_URL"] = "https://relay.example.com"
os.environ["SERVER_API_KEY"] = "test-server-key"
os.environ["ALLOWED_DOWNLOAD_HOSTS"] = "storage.example.com"

import httpx

from utils import http_client


class HttpRetryTest(unittest.IsolatedAsyncioTestCase):
    async def test_does_not_retry_non_idempotent_request_when_disabled(self):
        attempts = 0

        def handle(request: httpx.Request) -> httpx.Response:
            nonlocal attempts
            attempts += 1
            return httpx.Response(503, request=request)

        original_client = http_client._http_client
        http_client._http_client = httpx.AsyncClient(
            transport=httpx.MockTransport(handle)
        )

        try:
            with (
                self.assertLogs("utils.http_client", level="WARNING"),
                self.assertRaises(http_client.UpstreamRequestError),
            ):
                await http_client.request_with_retry(
                    "POST",
                    "https://spring.example.com/non-idempotent",
                    retry=False,
                )
        finally:
            await http_client._http_client.aclose()
            http_client._http_client = original_client

        self.assertEqual(1, attempts)


if __name__ == "__main__":
    unittest.main()
