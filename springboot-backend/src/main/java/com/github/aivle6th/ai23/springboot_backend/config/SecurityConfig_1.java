//package com.github.aivle6th.ai23.springboot_backend.config;
//
//import com.github.aivle6th.ai23.springboot_backend.util.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig_1 {
//    private final JwtUtil jwtUtil;
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
////    @Bean
////    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////        http
////                .csrf(AbstractHttpConfigurer::disable)
////                .sessionManagement(session -> session
////                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                )
////                .formLogin(AbstractHttpConfigurer::disable)
////                .httpBasic(AbstractHttpConfigurer::disable)
////                .addFilterBefore(new com.github.aivle6th.ai23.springboot_backend.security.filter.JwtAuthenticationFilter(jwtUtil),
////                        UsernamePasswordAuthenticationFilter.class)
////                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers("/api/auth/**").permitAll()
////                        .requestMatchers("/api/boards/**").authenticated()
////                        .anyRequest().authenticated()
////                );
////
////        return http.build();
////    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                );
//
//        return http.build();
//    }
//}
// 희현이 거 test를 위해 주석처리
