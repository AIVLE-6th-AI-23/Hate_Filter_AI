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

import com.github.aivle6th.ai23.springboot_backend.service.CustomUserDetailsService;
import com.github.aivle6th.ai23.springboot_backend.util.UserStatusManager;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
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
        .authorizeHttpRequests(auth -> auth
            // 공개 경로 허용
            .requestMatchers("/login", "/api/user/signup").permitAll()

            // 관리자 경로
            .requestMatchers("/admin/**").hasRole("ADMIN")

            // Board, Post 경로(인증 필요)
            .requestMatchers("/api/**").authenticated()

            // Swagger 경로
            .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()

            // 이 외의 경로(인증 필요)
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginProcessingUrl("/api/user/login")
            .successHandler(customAuthenticationSuccessHandler)
            .failureHandler(customAuthenticationFailureHandler)
            .permitAll()
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
}
