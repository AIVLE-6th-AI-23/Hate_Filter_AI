package com.github.aivle6th.ai23.springboot_backend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnalysisCompleteRequestDTO {
    private final String employeeId;
    private final Long postId;
    private final Long boardId;
    private final String resultSummary;
}
