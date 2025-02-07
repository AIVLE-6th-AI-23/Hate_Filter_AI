package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    public UserProfileResponseDto(User user) {
        this.userName = user.getUserName();
        this.deptId = user.getDepartment().getDeptId();
    }

    private String userName;
    private String deptId;
}
