package com.github.aivle6th.ai23.springboot_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.UserLoginRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.UserLoginResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.UserSignupRequestDto;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<String>> signup(@RequestBody UserSignupRequestDto user) {
        String response = userService.signup(user);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "회원가입 성공", response));
    }

    // @PostMapping("/login")
    // public ResponseEntity<ApiResponseDto<UserLoginResponseDto>> login(@RequestBody UserLoginRequestDto loginRequestDto) {
    //     User user = userService.login(loginRequestDto.getEmployeeId(), loginRequestDto.getPassword());
    //     UserLoginResponseDto response = new UserLoginResponseDto(user);
    //     return ResponseEntity.ok(new ApiResponseDto<>(true, "로그인 성공", response));
    // }

    // @PostMapping("/logout")
    // public ResponseEntity<ApiResponseDto<String>> logout() {
    //     return ResponseEntity.ok(new ApiResponseDto<>(true, "로그아웃 성공", "Logout successful"));
    // }
}
