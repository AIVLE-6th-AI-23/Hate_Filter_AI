package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserSignupRequestDto {

    @NotEmpty(message = "사원번호는 필수 입력값입니다")
    private String employeeId;

    @NotEmpty(message = "이름은 필수 입력값입니다")
    private String userName;

    @NotEmpty(message = "이메일은 필수 입력값입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력값입니다")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    private String password;

    private Long deptId;

    // User 엔티티로 변환하는 메소드
    public User toEntity() {
        User user = new User();
        user.setEmployeeId(this.employeeId);
        user.setUserName(this.userName);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}

