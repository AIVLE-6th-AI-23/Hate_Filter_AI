package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DEPARTMENT")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department {
    @Id
    @Column(name = "dept_id", nullable = false)
    private String deptId;

    @Column(name = "dept_name", nullable = false)
    private String deptName;

    @Builder
    public Department(String deptId, String deptName, String description) {
        this.deptId = deptId;
        this.deptName = deptName;
    }
}

