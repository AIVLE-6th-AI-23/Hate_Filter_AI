package com.github.aivle6th.ai23.springboot_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    private String employeeId;
    private String userName;
    private String email;
    private String password;
    private Long deptId;
}