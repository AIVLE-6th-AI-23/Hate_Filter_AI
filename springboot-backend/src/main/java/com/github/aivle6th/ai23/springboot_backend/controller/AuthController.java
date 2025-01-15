package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponse;
import com.github.aivle6th.ai23.springboot_backend.dto.LoginRequest;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody User user) {
        User savedUser = userService.signup(user);
        return ResponseEntity.ok(new ApiResponse<>(true, "회원가입 성공", savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest) {
        String result = userService.login(loginRequest.getEmployeeId(), loginRequest.getPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, "로그인 성공", result));
    }
}