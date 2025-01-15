package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "AUDIT_LOG")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "dept_id")
    private Long deptId;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "action_metadata")
    private String actionMetadata;

    @Column(name = "action_time")
    private LocalDateTime actionTime;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "dept_id", insertable = false, updatable = false)
    private Department department;
}
