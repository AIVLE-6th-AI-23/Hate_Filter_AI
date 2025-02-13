package com.github.aivle6th.ai23.springboot_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.github.aivle6th.ai23.springboot_backend.dto.AnalysisStartRequestDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentAnalysisAIService {
    @Value("${server.ai.url}")
    private String aiServerUrl;

    private final RestTemplate restTemplate;

    public Boolean start(AnalysisStartRequestDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AnalysisStartRequestDTO> httpRequest = new HttpEntity<>(request, headers);
        return restTemplate.postForObject(aiServerUrl + "/analyze/start", httpRequest, Boolean.class);
    }
    
}
