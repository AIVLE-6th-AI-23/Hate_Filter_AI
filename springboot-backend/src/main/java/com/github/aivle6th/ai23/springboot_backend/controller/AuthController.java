package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.LoginRequestDto;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<User>> signup(@RequestBody User user) {
        User savedUser = userService.signup(user);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "회원가입 성공", savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<String>> login(@RequestBody LoginRequestDto loginRequestDto) {
        String result = userService.login(loginRequestDto.getEmployeeId(), loginRequestDto.getPassword());
        return ResponseEntity.ok(new ApiResponseDto<>(true, "로그인 성공", result));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<String>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new ApiResponseDto<>(true, "로그아웃 성공", "Logout successful"));
    }
}
