package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Department {
    @Id
    @Column(name = "dept_id", nullable = false)
    private String deptId;

    @Column(name = "dept_name", nullable = false)
    private String deptName;
}
