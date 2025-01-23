package com.github.aivle6th.ai23.springboot_backend.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.github.aivle6th.ai23.springboot_backend.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        // 인증된 사용자 정보 가져오기
        UserDetails user = (UserDetails) authentication.getPrincipal();
        
        // 사용자 상태 변경
        userService.updateUserActiveStatus(user.getUsername(), true);
        
        // 세션 ID 확인
        String sessionId = request.getSession().getId();

        // JSON 응답 작성
        response.getWriter().write(String.format(
            "{\"message\": \"Login successful\", \"sessionId\": \"%s\", \"username\": \"%s\"}",
            sessionId, user.getUsername()
        ));
        response.getWriter().flush();
    }
}
