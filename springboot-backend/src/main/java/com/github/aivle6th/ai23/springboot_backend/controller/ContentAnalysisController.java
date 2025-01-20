package com.github.aivle6th.ai23.springboot_backend.controller;


import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.ContentAnalysisCreateRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.ContentAnalysisResponseDto;
import com.github.aivle6th.ai23.springboot_backend.entity.ContentAnalysis;
import com.github.aivle6th.ai23.springboot_backend.service.ContentAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/{boardId}/{postId}")
@RequiredArgsConstructor
public class ContentAnalysisController {

    private final ContentAnalysisService contentAnalysisService;

    @GetMapping("/content-analysis")
    public ResponseEntity<ApiResponseDto<ContentAnalysisResponseDto>>getContentAnalysis(@PathVariable Long postId){
        ContentAnalysisResponseDto contentAnalysis = contentAnalysisService.getContentAnalysisWithPost(postId);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "분석결과 조회 성공", contentAnalysis ));
    }

    @PostMapping("/content-analysis")
    public ResponseEntity<ApiResponseDto<ContentAnalysisResponseDto>>createContentAnalysis(
            @RequestBody ContentAnalysisCreateRequestDto requestDto,
            @PathVariable Long postId){
        // 서비스 호출
        ContentAnalysis contentAnalysis = contentAnalysisService.createContentAnalysis(
                requestDto.getContentAnalysisRequestDto(),
                requestDto.getAnalysisCategoryResultRequestDto(),
                postId
        );
        log.info(requestDto.getContentAnalysisRequestDto().getContentType());


        ContentAnalysisResponseDto savedresponseDto = ContentAnalysisResponseDto.analysisToDto(contentAnalysis);
        return ResponseEntity.ok(new ApiResponseDto<>(true , "분석결과 생성 성공" ,  savedresponseDto));
    }
}
