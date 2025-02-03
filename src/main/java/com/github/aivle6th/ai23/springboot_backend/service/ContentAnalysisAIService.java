package com.github.aivle6th.ai23.springboot_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.github.aivle6th.ai23.springboot_backend.dto.AnalysisStartRequestDTO;

@Service
public class ContentAnalysisAIService {
    @Value("${content-analysis.ai-server.url}")
    private String aiServerUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String start(AnalysisStartRequestDTO request) {
        return restTemplate.postForObject(aiServerUrl + "/analyze", request, String.class);
    }
    
}
