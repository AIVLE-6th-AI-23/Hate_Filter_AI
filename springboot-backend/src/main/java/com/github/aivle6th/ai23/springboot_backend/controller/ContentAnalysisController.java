package com.github.aivle6th.ai23.springboot_backend.controller;


import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.ContentAnalysisCreateRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.ContentAnalysisResponseDto;
import com.github.aivle6th.ai23.springboot_backend.entity.ContentAnalysis;
import com.github.aivle6th.ai23.springboot_backend.service.ContentAnalysisService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/{postId:\\d+}")
@RequiredArgsConstructor
@Tag(name = "Content Analysis API", description = "게시물 분석 결과 관리 API")
public class ContentAnalysisController {

    private final ContentAnalysisService contentAnalysisService;

    @Operation(summary = "분석 결과 조회", description = "특정 게시물에 대한 분석 결과를 조회합니다.")
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @GetMapping("/content-analysis")
    public ResponseEntity<ApiResponseDto<ContentAnalysisResponseDto>> getContentAnalysis(
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId) {
        ContentAnalysisResponseDto contentAnalysis = contentAnalysisService.getContentAnalysisWithPost(postId);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "분석 결과 조회 성공", contentAnalysis));
    }

    @Operation(summary = "분석 결과 생성", description = "특정 게시물에 대한 분석 결과를 생성합니다.")
    @PostMapping("/content-analysis")
    // TODO AI 서버에서만 호출 가능하도록 설정하기
    public ResponseEntity<ApiResponseDto<ContentAnalysisResponseDto>> createContentAnalysis(
            @RequestBody ContentAnalysisCreateRequestDto requestDto,
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId) {
        // 서비스 호출
        ContentAnalysis contentAnalysis = contentAnalysisService.createContentAnalysis(
                requestDto.getContentAnalysisRequestDto(),
                requestDto.getAnalysisCategoryResultRequestDto(),
                postId
        );
        log.info(requestDto.getContentAnalysisRequestDto().getContentType());

        ContentAnalysisResponseDto savedresponseDto = ContentAnalysisResponseDto.analysisToDto(contentAnalysis);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "분석 결과 생성 성공", savedresponseDto));
    }
}
