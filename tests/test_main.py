import os
import tempfile
import unittest
from pathlib import Path
from unittest.mock import AsyncMock, patch
from uuid import UUID

os.environ["OPENAI_API_KEY"] = "test-openai-key"
os.environ["BASE_API_URL"] = "https://spring.example.com"
os.environ["RELAY_SERVER_URL"] = "https://relay.example.com"
os.environ["SERVER_API_KEY"] = "test-server-key"
os.environ["ALLOWED_DOWNLOAD_HOSTS"] = "storage.example.com"

import httpx

import main
from utils.type import AnalysisStartRequestDTO


class MainApiTest(unittest.IsolatedAsyncioTestCase):
    async def test_analysis_endpoint_requires_key_and_accepts_relay_contract(self):
        transport = httpx.ASGITransport(app=main.app)
        request_body = {
            "requestId": "5b78e528-3f12-4d50-8525-9ff2b66a30e4",
            "employeeId": "employee",
            "boardId": 1,
            "postId": 2,
            "thumbnail": "https://storage.example.com/file.txt",
        }

        with patch.object(main, "analyze", new=AsyncMock()) as analyze_mock:
            async with main.app.router.lifespan_context(main.app):
                async with httpx.AsyncClient(
                    transport=transport,
                    base_url="http://testserver",
                ) as client:
                    health_response = await client.get("/health")
                    self.assertEqual(200, health_response.status_code)

                    unauthorized_response = await client.post(
                        "/analyze/start",
                        json=request_body,
                    )
                    self.assertEqual(401, unauthorized_response.status_code)

                    accepted_response = await client.post(
                        "/analyze/start",
                        headers={"X-API-KEY": "test-server-key"},
                        json=request_body,
                    )
                    self.assertEqual(202, accepted_response.status_code)
                    self.assertIs(True, accepted_response.json())

                    duplicate_response = await client.post(
                        "/analyze/start",
                        headers={"X-API-KEY": "test-server-key"},
                        json=request_body,
                    )
                    self.assertEqual(202, duplicate_response.status_code)
                    self.assertEqual(1, analyze_mock.await_count)

        accepted_request = analyze_mock.await_args.args[0]
        self.assertEqual(
            UUID("5b78e528-3f12-4d50-8525-9ff2b66a30e4"),
            accepted_request.requestId,
        )


class AnalysisLifecycleTest(unittest.IsolatedAsyncioTestCase):
    async def test_notifies_relay_even_when_download_fails(self):
        request = self.request()

        with (
            self.assertLogs("main", level="ERROR"),
            patch.object(main, "update_spring_status", new=AsyncMock()) as update_mock,
            patch.object(
                main,
                "download_file_from_url",
                new=AsyncMock(side_effect=RuntimeError("download failed")),
            ),
            patch.object(main, "notify_relay_ready", new=AsyncMock()) as relay_mock,
        ):
            await main.analyze(request)

        self.assertEqual(2, update_mock.await_count)
        relay_mock.assert_awaited_once_with(
            str(request.requestId),
            request.boardId,
            request.postId,
        )

    async def test_removes_download_and_notifies_relay_after_success(self):
        request = self.request()

        with tempfile.NamedTemporaryFile(delete=False) as downloaded_file:
            downloaded_path = downloaded_file.name

        with (
            patch.object(main, "update_spring_status", new=AsyncMock()),
            patch.object(
                main,
                "download_file_from_url",
                new=AsyncMock(return_value=downloaded_path),
            ),
            patch.object(main, "categorize_content_file", return_value="text"),
            patch.object(main, "run_content_analysis", new=AsyncMock(return_value=[])),
            patch.object(main, "exit_status", new=AsyncMock()) as exit_mock,
            patch.object(main, "notify_relay_ready", new=AsyncMock()) as relay_mock,
        ):
            await main.analyze(request)

        self.assertFalse(Path(downloaded_path).exists())
        exit_mock.assert_awaited_once()
        relay_mock.assert_awaited_once_with(
            str(request.requestId),
            request.boardId,
            request.postId,
        )

    @staticmethod
    def request():
        return AnalysisStartRequestDTO(
            requestId="5b78e528-3f12-4d50-8525-9ff2b66a30e4",
            employeeId="employee",
            boardId=1,
            postId=2,
            thumbnail="https://storage.example.com/file.txt",
        )


if __name__ == "__main__":
    unittest.main()
