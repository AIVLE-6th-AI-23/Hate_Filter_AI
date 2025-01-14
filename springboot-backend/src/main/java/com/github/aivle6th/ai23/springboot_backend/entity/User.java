package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "USER")
public class User {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "dept_id")
    private Long deptId;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user")
    private List<AuditLog> auditLogs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "dept_id", insertable = false, updatable = false)
    private Department department;
}