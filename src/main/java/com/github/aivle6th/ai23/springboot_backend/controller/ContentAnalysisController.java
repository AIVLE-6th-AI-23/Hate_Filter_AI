package com.github.aivle6th.ai23.springboot_backend.controller;


import com.github.aivle6th.ai23.springboot_backend.dto.AnalysisCompleteRequestDTO;
import com.github.aivle6th.ai23.springboot_backend.dto.AnalysisStartRequestDTO;
import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.ContentAnalysisCreateRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.ContentAnalysisResponseDto;
import com.github.aivle6th.ai23.springboot_backend.entity.ContentAnalysis;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.service.ContentAnalysisService;
import com.github.aivle6th.ai23.springboot_backend.service.MailService;
import com.github.aivle6th.ai23.springboot_backend.service.PostService;
import com.github.aivle6th.ai23.springboot_backend.service.PushNotificationService;
import com.github.aivle6th.ai23.springboot_backend.service.UserService;
import com.github.aivle6th.ai23.springboot_backend.service.ContentAnalysisAIService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Subscription;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/{postId:\\d+}")
@RequiredArgsConstructor
@Tag(name = "Content Analysis API", description = "게시물 분석 결과 관리 API")
public class ContentAnalysisController {

    private final ContentAnalysisService contentAnalysisService;
    private final PushNotificationService notificationService;
    private final ContentAnalysisAIService contentAnalysisAIService;
    private final PostService postService;
    private final UserService userService;
    private final MailService mailService;

    @Operation(summary = "분석 결과 조회", description = "특정 게시물에 대한 분석 결과를 조회합니다.")
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @GetMapping("/content-analysis")
    public ResponseEntity<ApiResponseDto<ContentAnalysisResponseDto>> getContentAnalysis(
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId) {
        try {
            ContentAnalysisResponseDto contentAnalysis = contentAnalysisService.getContentAnalysisWithPost(postId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "분석 결과 조회 성공", contentAnalysis));
        } catch (Exception e) {
            log.error("분석 결과 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "분석 결과 조회 중 문제가 발생했습니다.", null));
        }
    }
    @Operation(summary = "분석 결과 생성", description = "특정 게시물에 대한 분석 결과를 생성합니다.")
    @PostMapping("/content-analysis/create")
    public ResponseEntity<ApiResponseDto<ContentAnalysisResponseDto>> createContentAnalysis(
            @RequestBody ContentAnalysisCreateRequestDto requestDto,
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId) {
        try {
            ContentAnalysis contentAnalysis = contentAnalysisService.createContentAnalysis(
                    requestDto.getContentAnalysisRequestDto(),
                    requestDto.getAnalysisCategoryResultRequestDto(),
                    postId
            );
            log.info(requestDto.getContentAnalysisRequestDto().getContentType());

            ContentAnalysisResponseDto savedresponseDto = ContentAnalysisResponseDto.analysisToDto(contentAnalysis);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "분석 결과 생성 성공", savedresponseDto));
        } catch (Exception e) {
            log.error("분석 결과 생성 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "분석 결과 생성 중 문제가 발생했습니다.", null));
        }
    }

    @Operation(summary = "분석 시작", description = "AI 서버에 요청하여 특정 게시물에 대한 분석을 시작합니다.")
    @PostMapping("/content-analysis/start")
    public ResponseEntity<ApiResponseDto<Void>> startContentAnalysis(
        @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId
    ){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseDto<>(false, "Unauthorized access", null));
            }

            String employeeId = ((UserDetails) authentication.getPrincipal()).getUsername();
            
            AnalysisStartRequestDTO request = new AnalysisStartRequestDTO(postService.getPostById(postId), employeeId);
             
            String message = contentAnalysisAIService.start(request);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "분석 시작 성공" + message, null));
        } catch (IllegalArgumentException e) {
            log.error("분석 시작 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, "잘못된 요청 입니다.", null));
        } catch (Exception e) {
            log.error("분석 시작 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "분석 시작 중 문제가 발생했습니다.", null));
        }
    }
    
    @Operation(summary = "분석 완료 시 알림 전송", description = "AI 분석 완료 시 client에게 알림을 전송합니다.")
    @PostMapping("/content-analysis/notifications")
    public ResponseEntity<ApiResponseDto<Void>> sendPushNotification(@RequestBody AnalysisCompleteRequestDTO analysisCompleteRequestDTO ) {
        try{
            notificationService.sendPushNotification(new Subscription(
                analysisCompleteRequestDTO.getSubscriptionEndpoint(), 
                new Subscription.Keys(analysisCompleteRequestDTO.getSubscriptionKeyp256h(), analysisCompleteRequestDTO.getSubscriptionKeyAuth())
            ), 
            analysisCompleteRequestDTO.getResultSummary());
            
            User user = userService.getUserByEmployeeId(analysisCompleteRequestDTO.getEmployeeId());
            mailService.sendContentAnalysisNotificationEmail(user.getEmail(), analysisCompleteRequestDTO.getResultSummary());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "알림 전송 성공", null));
        } catch (IllegalArgumentException e) {
            log.error("알림 전송 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false,"잘못된 요청 입니다.", null));
        } catch (Exception e) {
            log.error("알림 전송 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "알림 전송 중 서버에서 문제가 발생했습니다.", null));
        }
    }
}
