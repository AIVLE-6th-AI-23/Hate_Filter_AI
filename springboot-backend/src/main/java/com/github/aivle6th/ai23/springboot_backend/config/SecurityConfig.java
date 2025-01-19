package com.github.aivle6th.ai23.springboot_backend.config;

import com.github.aivle6th.ai23.springboot_backend.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity          // Spring Security 활성화 하고 웹 보안 설정을 구성하는데 사용하는 Annotation
@Configuration              // 해당 클래스가 @Bean 메서드를 포함하고 있으며, 스프링 컨테이너에 의해 빈 정의를 생성하는 데 사용할 수 있다.
public class SecurityConfig {

    @Autowired
    private CustomUserDetailService userDetailService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/join", "/jointest").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())       // for test
                // .formLogin(Customizer.withDefaults());
                .formLogin(form -> form
                        .usernameParameter("employeeId")
                        .passwordParameter("pwd")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                );
        return http.build();
    }
}

