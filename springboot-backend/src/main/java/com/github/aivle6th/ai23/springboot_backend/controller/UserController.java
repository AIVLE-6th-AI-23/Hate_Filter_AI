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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<String>> signup(@RequestBody UserSignupRequestDto user) {
        String response = userService.signup(user);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "회원가입 성공", response));
    }

    @PostMapping("/update") // 비밀 번호 변경 시 세션 재요구
    public ResponseEntity<ApiResponseDto<String>> update(@RequestBody UserSignupRequestDto user,
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
