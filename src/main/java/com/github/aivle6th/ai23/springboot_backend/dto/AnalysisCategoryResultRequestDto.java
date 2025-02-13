package com.github.aivle6th.ai23.springboot_backend.dto;

import java.util.Map;

import com.github.aivle6th.ai23.springboot_backend.entity.AnalysisCategoryResult;
import com.github.aivle6th.ai23.springboot_backend.entity.ContentAnalysis;
import com.github.aivle6th.ai23.springboot_backend.entity.HateCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class AnalysisCategoryResultRequestDto {

    private final Float categoryScore;
    private final Map<String, Object> detectionMetadata;
    private final String categoryName;

    //Dto -> Entity 변환
    public  AnalysisCategoryResult toEntity(HateCategory hateCategory, ContentAnalysis contentAnalysis){
        return AnalysisCategoryResult.builder()
                .categoryScore(categoryScore)
                .detectionMetadata(detectionMetadata)
                .contentAnalysis(contentAnalysis)
                .hateCategory(hateCategory)
                .build();
    }
}
