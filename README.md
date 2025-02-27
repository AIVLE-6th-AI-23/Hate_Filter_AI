# Hate Filter AI Server

Relay Server에서 전달받은 콘텐츠를 텍스트·이미지·영상 유형에 따라 분석하고 결과를 Spring Boot 서버에 저장하는 FastAPI 서비스입니다.

## 분석 흐름

1. Relay가 `POST /analyze/start`로 분석 요청과 고유한 `requestId`를 전달합니다.
2. AI 서버는 백그라운드 작업을 등록하고 `202 Accepted`를 반환합니다.
   동일한 `requestId`가 재전송되면 새 분석을 만들지 않고 기존 요청으로 처리합니다.
3. 허용된 HTTPS 저장소에서 콘텐츠를 제한된 크기로 내려받습니다.
4. 콘텐츠 유형에 맞는 분석을 실행하고 Spring Boot에 진행 상태와 결과를 전달합니다.
5. 성공·실패 여부와 관계없이 `POST {RELAY_SERVER_URL}/status/ok`로 완료 신호를 전송합니다.
6. Relay는 `requestId`, `boardId`, `postId`가 현재 작업과 모두 일치할 때 다음 요청을 처리합니다.

서버 간 API에는 `X-API-KEY` 헤더가 필요합니다. 이 서비스는 쿠키 인증을 사용하지 않으므로 CSRF 토큰 대신 서버 간 API 키로 호출자를 검증합니다.

## 환경변수

| 이름 | 필수 | 기본값 | 설명 |
| --- | --- | --- | --- |
| `OPENAI_API_KEY` | 예 | 없음 | 텍스트 분석에 사용하는 API 키 |
| `BASE_API_URL` | 예 | 없음 | Spring Boot 서버 기본 URL |
| `RELAY_SERVER_URL` | 예 | 없음 | Relay Server 기본 URL |
| `SERVER_API_KEY` | 예 | 없음 | 서버 간 요청에 사용하는 API 키 |
| `ALLOWED_DOWNLOAD_HOSTS` | 예 | 없음 | 콘텐츠 다운로드를 허용할 정확한 호스트 목록. 쉼표로 구분 |
| `SAVE_DIRECTORY` | 아니요 | `/tmp/hate-filter-ai/downloads` | 임시 다운로드 경로 |
| `MAX_DOWNLOAD_BYTES` | 아니요 | `104857600` | URL 다운로드 최대 크기 |
| `MAX_UPLOAD_BYTES` | 아니요 | `104857600` | 직접 분석 API 업로드 최대 크기 |
| `REQUEST_DEDUP_CACHE_SIZE` | 아니요 | `10000` | 프로세스가 기억할 최근 분석 요청 ID 개수 |
| `HTTP_TIMEOUT_SECONDS` | 아니요 | `15` | 서버 간 HTTP 요청 타임아웃 |
| `HTTP_MAX_RETRIES` | 아니요 | `3` | 일시적 오류의 최대 시도 횟수 |
| `HTTP_RETRY_BASE_DELAY_SECONDS` | 아니요 | `1` | 지수 백오프 최초 대기 시간 |

`ALLOWED_DOWNLOAD_HOSTS`에는 URL 전체가 아니라 호스트만 입력합니다.

```bash
ALLOWED_DOWNLOAD_HOSTS=storage-account.blob.core.windows.net
```

## API

### 분석 요청

```http
POST /analyze/start
X-API-KEY: ...
Content-Type: application/json
```

```json
{
  "requestId": "5b78e528-3f12-4d50-8525-9ff2b66a30e4",
  "employeeId": "employee-id",
  "boardId": 1,
  "postId": 1,
  "thumbnail": "https://storage-account.blob.core.windows.net/content/file.txt"
}
```

- 정상 등록: `202 Accepted`
- API 키 누락 또는 불일치: `401 Unauthorized`
- 잘못된 ID·URL·요청 형식: `422 Unprocessable Entity`

### 직접 분석 API

- `POST /detect/text`
- `POST /detect/image`
- `POST /detect/video`

세 API 모두 `X-API-KEY` 인증을 요구합니다. 이미지와 영상은 `MAX_UPLOAD_BYTES`를 초과하면 `413`을 반환합니다.

### 상태 확인

`GET /health`는 인증 없이 서비스 프로세스의 응답 여부를 확인합니다.

## 실행

```bash
cp .env.example .env.dev
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --workers 1
```

AI 모델은 메모리 사용량이 크므로 Docker와 Azure 실행 설정은 worker 하나를 사용합니다. 여러 인스턴스로 확장할 때에는 Relay의 동시 처리 정책과 모델 자원 사용량을 함께 검토해야 합니다.

중복 요청 기록은 프로세스 메모리에 저장됩니다. 여러 AI 서버 인스턴스에서 같은 `requestId`의 중복 실행까지 막아야 하는 환경에서는 Redis나 데이터베이스 기반 멱등성 저장소가 필요합니다.

## 테스트

```bash
python -m unittest discover -s tests -v
```
