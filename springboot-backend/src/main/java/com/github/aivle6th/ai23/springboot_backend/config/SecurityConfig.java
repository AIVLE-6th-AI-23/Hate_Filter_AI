package com.github.aivle6th.ai23.springboot_backend.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf ->csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // 공개 경로 허용
            .requestMatchers("/login", "/signup").permitAll()

            // 관리자 경로
            .requestMatchers("/admin/**").hasRole("ADMIN")

            .requestMatchers("/api/boards/**", "/api/**/posts/**").authenticated()
            .anyRequest().permitAll()

            // // 프로젝트 경로 - 동적 권한 확인
            // .requestMatchers("/api/boards/{boardId}/**").access((authentication, request) -> {
            //     Long boardId = (Long) request.getRequest().getAttribute("boardId");
            //     if (boardId == null) {
            //         return new AuthorizationDecision(false);
            //     }
            //     boolean hasAccess = securityService.canAccessBoard(authentication.get(), boardId);
            //     return new AuthorizationDecision(hasAccess);
            // })

            // // 게시글 경로 - 동적 권한 확인
            // .requestMatchers("/api/{boardId}/posts/{postId}/**").access((authentication, request) -> {
                
            //     Long postId = (Long) request.getRequest().getAttribute("postId");
            //     if (postId == null) {
            //         return new AuthorizationDecision(false);
            //     }
            //     boolean hasAccess = securityService.canAccessPost(authentication.get(), postId);
            //     return new AuthorizationDecision(hasAccess);
            // })

            // 그 외 모든 요청은 인증 필요
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login") // 커스텀 로그인 페이지
            .defaultSuccessUrl("/dashboard", true) // 로그인 성공 시 리다이렉트 경로
            .failureUrl("/login?error=true") // 로그인 실패 시 리다이렉트 경로
            .permitAll()
        ) // formlogin -> spring mvc 와 관련성??
        .logout(logout -> logout
            .logoutUrl("/logout") // 로그아웃 URL
            .logoutSuccessUrl("/login?logout=true") // 로그아웃 성공 시 리다이렉트 경로
            .permitAll()
        );


        return http.build();
    }
}
