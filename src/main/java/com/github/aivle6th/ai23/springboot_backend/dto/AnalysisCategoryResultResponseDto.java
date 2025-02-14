package com.github.aivle6th.ai23.springboot_backend.dto;

import java.util.Map;

import com.github.aivle6th.ai23.springboot_backend.entity.AnalysisCategoryResult;
import com.github.aivle6th.ai23.springboot_backend.entity.HateCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class AnalysisCategoryResultResponseDto {
    private final Long resultId;
    private final Float categoryScore;
    private final Map<String, Object> detectionMetadata;
    private final String categoryName;
    private final String description;
    private final Long severityLevel;

    public static AnalysisCategoryResultResponseDto resultToDto(AnalysisCategoryResult analysisCategoryResult){
        HateCategory hateCategory = analysisCategoryResult.getHateCategory();

        return AnalysisCategoryResultResponseDto.builder()
                .resultId(analysisCategoryResult.getResultId())
                .categoryScore(analysisCategoryResult.getCategoryScore())
                .detectionMetadata(analysisCategoryResult.getDetectionMetadata())
                .categoryName(hateCategory.getCategoryName())
                .description(hateCategory.getDescription())
                .severityLevel(hateCategory.getSeverityLevel())
                .build();
    }
}
