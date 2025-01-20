package com.github.aivle6th.ai23.springboot_backend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Getter
@RequiredArgsConstructor
public class ContentAnalysisCreateRequestDto {

    private final ContentAnalysisRequestDto contentAnalysisRequestDto;
    private final List<AnalysisCategoryResultRequestDto> analysisCategoryResultRequestDto;

}
