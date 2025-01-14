package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.User;
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
}