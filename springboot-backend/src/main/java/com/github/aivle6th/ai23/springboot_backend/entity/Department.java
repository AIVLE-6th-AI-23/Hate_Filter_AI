package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DEPARTMENT")
public class Department {
    @Id
    @Column(name = "dept_id")
    private String deptId;

    @Column(name = "dept_name")
    private String deptName;

    @OneToMany(mappedBy = "department")
    private List<User> users = new ArrayList<>();

    @ManyToMany(mappedBy = "departments")
    private Set<Board> boards = new HashSet<>();
}