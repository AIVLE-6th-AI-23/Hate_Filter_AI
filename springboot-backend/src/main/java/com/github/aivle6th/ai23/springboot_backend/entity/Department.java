package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "DEPARTMENT")
public class Department {
    @Id
    @Column(name = "dept_id")
    private String deptId;

    @Column(name = "dept_name")
    private String deptName;

    @OneToMany(mappedBy = "department")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "department")
    private List<BoardDepartment> boardDepartments = new ArrayList<>();
}