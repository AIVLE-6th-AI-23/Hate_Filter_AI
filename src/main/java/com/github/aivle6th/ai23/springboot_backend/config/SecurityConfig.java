package com.github.aivle6th.ai23.springboot_backend.config;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.ForwardedHeaderFilter;

import com.github.aivle6th.ai23.springboot_backend.service.CustomUserDetailsService;
import com.github.aivle6th.ai23.springboot_backend.util.UserStatusManager;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig{
    private final CustomUserDetailsService customUserDetailsService;
    private final UserStatusManager userStatusManager;
    private final PasswordEncoderConfig passwordEncoderConfig;

    @Value("${server.ai.url}")
    private String AIServerUrl;
    @Value("${server.fe.url}")
    private String FEServerUrl;
    @Value("${server.ai.key}")
    private String APIKEY;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Profile("dev")
    public SecurityFilterChain filterChain_development(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())                
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/*/posts/*/status*", "/api/*/content-analysis/notifications", "/api/*/content-analysis/create").access((request, context) -> {
                String method = context.getRequest().getMethod();
                String apiKey = context.getRequest().getHeader("X-API-KEY");
                // API 키 인증
                boolean isAllowedAPIkey = apiKey.equals(APIKEY);   
                // POST 또는 PATCH 요청만 허용
                boolean isAllowedMethod = "POST".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method);

                return new AuthorizationDecision(isAllowedAPIkey && isAllowedMethod);
            })

            // 공개 경로 허용
            .requestMatchers("/api/user/login", "/api/user/signup", "/api/user/profile", "/api/user/verify", "/api/user/checkid/*", "/api/user/password/reset").permitAll()

            // Swagger 경로
            .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()

            // 관리자 경로
            .requestMatchers("/admin/**").hasRole("ADMIN")

            // Board, Post 경로(인증 필요)
            .requestMatchers("/api/**").authenticated()

            // 이 외의 경로(인증 필요)
            .anyRequest().authenticated()
        )
        .userDetailsService(customUserDetailsService)
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
    @Profile("prod")
    public SecurityFilterChain filterChain_production(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/api/*/posts/*/status*", "/api/*/content-analysis/notifications", "/api/*/content-analysis/create", "/api/user/login", "/api/user/signup", "/api/user/profile", "/api/user/verify", "/api/user/checkid/*", "/api/user/password/reset", "/api/user/logout")    
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/*/posts/*/status/**", "/api/*/content-analysis/notifications", "/api/*/content-analysis/create").access((request, context) -> {
                String method = context.getRequest().getMethod();
                String apiKey = context.getRequest().getHeader("X-API-KEY");
                boolean isAllowedAPIkey = apiKey.equals(APIKEY);   
                // POST 또는 PATCH 요청만 허용
                boolean isAllowedMethod = "POST".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method);

                return new AuthorizationDecision(isAllowedAPIkey && isAllowedMethod);
            })

            // 공개 경로 허용
            .requestMatchers("/api/user/login", "/api/user/signup", "/api/user/profile", "/api/user/verify", "/api/user/checkid/*", "/api/user/password/reset").permitAll()

            // 관리자 경로
            .requestMatchers("/admin/**").hasRole("ADMIN")

            // Board, Post 경로(인증 필요)
            .requestMatchers("/api/**").authenticated()
            
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // 이 외의 경로(인증 필요)
            .anyRequest().authenticated()
        )
        .userDetailsService(customUserDetailsService)
        .logout(logout -> logout
            .logoutUrl("/api/user/logout") // 로그아웃 URL
            .invalidateHttpSession(true) // 세션 무효화
            .deleteCookies("JSESSIONID") // 쿠키 삭제
            .addLogoutHandler((request, response, authentication) -> {
                if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                    String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                    userStatusManager.deactivateUser(username);
                    SecurityContextHolder.clearContext();
                }
            })
            .logoutSuccessHandler((request, response, authentication) -> {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Logout successful\"}");
            })
            .permitAll()
        )
        .requiresChannel(channel -> channel.anyRequest().requiresSecure()) 
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
        configuration.addAllowedOrigin(FEServerUrl);
        configuration.addAllowedOrigin(AIServerUrl);
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 전송 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 적용
        return source;
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

}
