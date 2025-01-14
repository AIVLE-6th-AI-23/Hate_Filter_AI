package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "dept_id")
    private int deptId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Builder
    public User(int userId, String employeeId, String userName, String email, String password, int deptId, Boolean isActive) {
        this.userId = userId;
        this.employeeId = employeeId;
        this.password = password;
        this.userName = userName;
        this.email = email;
        this.deptId = deptId;
        this.isActive = isActive;
        this.createdAt = LocalDateTime.now();
        this.lastLogin = LocalDateTime.now();
    }
}