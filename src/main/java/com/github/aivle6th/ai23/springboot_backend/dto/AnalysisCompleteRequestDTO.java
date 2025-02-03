package com.github.aivle6th.ai23.springboot_backend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnalysisCompleteRequestDTO {
    private final String employeeId;
    private final String subscriptionEndpoint;
    private final String subscriptionKeyp256h;
    private final String subscriptionKeyAuth;
    private final String resultSummary;
}
