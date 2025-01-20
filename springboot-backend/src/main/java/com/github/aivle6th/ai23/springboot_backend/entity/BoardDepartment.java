package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "BOARD_DEPARTMENT")
@EqualsAndHashCode(of = "boardId")
public class BoardDepartment {
    @Id
    @Column(name = "board_dept_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardDeptId;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Department department;
}
