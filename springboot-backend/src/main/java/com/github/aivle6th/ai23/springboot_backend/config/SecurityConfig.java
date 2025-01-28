package com.github.aivle6th.ai23.springboot_backend.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.github.aivle6th.ai23.springboot_backend.service.CustomUserDetailsService;
import com.github.aivle6th.ai23.springboot_backend.util.UserStatusManager;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{
    private final CustomUserDetailsService customUserDetailsService;
    private final UserStatusManager userStatusManager;
    private final PasswordEncoderConfig passwordEncoderConfig;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf ->csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            // 공개 경로 허용
            .requestMatchers("api/user/login", "/api/user/signup").permitAll()

            // Swagger 경로
            .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()

            // 관리자 경로
            .requestMatchers("/admin/**").hasRole("ADMIN")

            // Board, Post 경로(인증 필요)
            .requestMatchers("/api/**").authenticated()

            // 이 외의 경로(인증 필요)
            .anyRequest().authenticated()
        )
        .logout(logout -> logout
            .logoutUrl("/api/user/logout") // 로그아웃 URL
            .invalidateHttpSession(true) // 세션 무효화
            .deleteCookies("JSESSIONID") // 쿠키 삭제
            .addLogoutHandler((request, response, authentication) -> {
                if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                    String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                    userStatusManager.deactivateUser(username);
                }
            })
            .logoutSuccessHandler((request, response, authentication) -> {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Logout successful\"}");
            })
            .permitAll()
        )
        .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 생성 활성화(기본 설정)
        );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoderConfig.passwordEncoder());
        return authProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // 허용할 클라이언트 도메인
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 전송 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 적용
        return source;
    }
}
