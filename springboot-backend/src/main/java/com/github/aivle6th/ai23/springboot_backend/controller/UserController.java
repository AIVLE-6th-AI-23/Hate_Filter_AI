package com.github.aivle6th.ai23.springboot_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.UserSignupRequestDto;
import com.github.aivle6th.ai23.springboot_backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관리 API")
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 회원가입 처리합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<String>> signup(
            @Parameter(description = "회원가입 요청 데이터", required = true) @RequestBody UserSignupRequestDto user) {
        String response = userService.signup(user);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "회원가입 성공", response));
    }

    @Operation(summary = "회원 정보 수정", description = "사용자 정보를 수정하고 비밀번호 변경 시 세션을 무효화합니다.")
    @PostMapping("/update")
    public ResponseEntity<ApiResponseDto<String>> update(
            @Parameter(description = "회원 정보 수정 요청 데이터", required = true) @RequestBody UserSignupRequestDto user,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            // 비밀번호 업데이트 로직
            String updateResponse = userService.updateUserInfo(user);

            // 현재 세션 무효화 처리
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

            return ResponseEntity.ok(new ApiResponseDto<>(true, "비밀번호 변경 성공, 재로그인이 필요합니다.", updateResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
}
