package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.ContentAnalysis;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class ContentAnalysisResponseDto {
    private final Long analysisId;
    private final String contentType;
    private final String analysisDetail;
    private final String status;
    private final LocalDateTime analysisAt;
    private final List<AnalysisCategoryResultResponseDto> analysisCategoryResultResponseDto;

    public static ContentAnalysisResponseDto analysisToDto(ContentAnalysis contentAnalysis){
        List<AnalysisCategoryResultResponseDto> result = contentAnalysis.getAnalysisCategoryResults().stream()
                .map(AnalysisCategoryResultResponseDto::resultToDto)
                .toList();

        return ContentAnalysisResponseDto.builder()
                .analysisId(contentAnalysis.getAnalysisId())
                .contentType(contentAnalysis.getContentType())
                .analysisDetail(contentAnalysis.getAnalysisDetail())
                .analysisAt(contentAnalysis.getAnalyzedAt())
                .status(contentAnalysis.getStatus())
                .analysisCategoryResultResponseDto(result)
                .build();
    }
}
