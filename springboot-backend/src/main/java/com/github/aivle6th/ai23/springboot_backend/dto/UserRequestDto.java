package com.github.aivle6th.ai23.springboot-backend.dto;

import com.github.aivle6th.ai23.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    private String email;
    private String password;
    private String username;

    @Builder
    public UserRequestDto(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .username(username)
                .build();
    }
}