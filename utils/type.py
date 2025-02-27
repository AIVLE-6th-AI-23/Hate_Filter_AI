from typing import Any, Dict, List
from uuid import UUID

from pydantic import AnyHttpUrl, BaseModel, Field, field_validator


class AnalysisStartRequestDTO(BaseModel):
    requestId: UUID
    employeeId: str = Field(min_length=1, max_length=255)
    postId: int = Field(gt=0)
    boardId: int = Field(gt=0)
    thumbnail: AnyHttpUrl

    @field_validator("thumbnail")
    @classmethod
    def require_https_content_url(cls, value: AnyHttpUrl) -> AnyHttpUrl:
        if value.scheme != "https":
            raise ValueError("thumbnail must use HTTPS")
        return value


class ContentAnalysisRequestDto(BaseModel):
    contentType: str
    analysisDetail: str


class AnalysisCategoryResultRequestDto(BaseModel):
    categoryName: str
    categoryScore: float
    detectionMetadata: Dict[str, Any]


class AnalysisRequest(BaseModel):
    contentAnalysisRequestDto: ContentAnalysisRequestDto
    analysisCategoryResultRequestDto: List[AnalysisCategoryResultRequestDto]
