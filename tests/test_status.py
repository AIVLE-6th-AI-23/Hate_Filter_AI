import json
import os
import unittest

os.environ["OPENAI_API_KEY"] = "test-openai-key"
os.environ["BASE_API_URL"] = "https://spring.example.com"
os.environ["RELAY_SERVER_URL"] = "https://relay.example.com"
os.environ["SERVER_API_KEY"] = "test-server-key"
os.environ["ALLOWED_DOWNLOAD_HOSTS"] = "storage.example.com"

import httpx

from utils import http_client
from utils.status import exit_status, notify_relay_ready, update_spring_status
from utils.type import AnalysisCategoryResultRequestDto, ContentAnalysisRequestDto


class StatusRequestTest(unittest.IsolatedAsyncioTestCase):
    async def test_sends_named_spring_params_and_correlated_relay_callback(self):
        requests = []

        def handle(request: httpx.Request) -> httpx.Response:
            requests.append(request)
            return httpx.Response(200, request=request, json={"ok": True})

        original_client = http_client._http_client
        http_client._http_client = httpx.AsyncClient(
            transport=httpx.MockTransport(handle)
        )

        try:
            await update_spring_status(10, 20, "Processing", 30)
            await notify_relay_ready(
                "5b78e528-3f12-4d50-8525-9ff2b66a30e4",
                10,
                20,
            )
        finally:
            await http_client._http_client.aclose()
            http_client._http_client = original_client

        spring_request, relay_request = requests
        self.assertEqual("test-server-key", spring_request.headers["X-API-KEY"])
        self.assertEqual("Processing", spring_request.url.params["status"])
        self.assertEqual("30", spring_request.url.params["progress"])

        self.assertEqual("test-server-key", relay_request.headers["X-API-KEY"])
        self.assertEqual("/status/ok", relay_request.url.path)
        self.assertEqual(
            {
                "requestId": "5b78e528-3f12-4d50-8525-9ff2b66a30e4",
                "boardId": 10,
                "postId": 20,
            },
            json.loads(relay_request.content),
        )

    async def test_persists_result_then_completes_and_notifies(self):
        requests = []

        def handle(request: httpx.Request) -> httpx.Response:
            requests.append(request)
            return httpx.Response(200, request=request, json={"ok": True})

        original_client = http_client._http_client
        http_client._http_client = httpx.AsyncClient(
            transport=httpx.MockTransport(handle)
        )

        try:
            await exit_status(
                10,
                20,
                "employee",
                [
                    AnalysisCategoryResultRequestDto(
                        categoryName="racial",
                        categoryScore=0.8,
                        detectionMetadata={"countries": ["001"]},
                    )
                ],
                ContentAnalysisRequestDto(
                    contentType="text",
                    analysisDetail="summary",
                ),
            )
        finally:
            await http_client._http_client.aclose()
            http_client._http_client = original_client

        self.assertEqual(
            [
                "/api/20/content-analysis/create",
                "/api/10/posts/20/status",
                "/api/20/content-analysis/notifications",
            ],
            [request.url.path for request in requests],
        )
        self.assertEqual("COMPLETED", requests[1].url.params["status"])
        self.assertEqual("100", requests[1].url.params["progress"])


if __name__ == "__main__":
    unittest.main()
