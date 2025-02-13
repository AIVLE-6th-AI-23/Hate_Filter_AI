package com.github.aivle6th.ai23.springboot_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserVerifyRequestDto {
    private String employeeId;
    private String deptId;
    private String email;
}
