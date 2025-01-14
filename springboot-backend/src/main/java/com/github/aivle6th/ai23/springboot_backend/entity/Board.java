package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "BOARD")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int boardId;

    @Column(name = "board_name", nullable = false)
    private String boardName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "end_date")
    private String endDate;

    @Builder
    public Board(String boardName, String description, boolean isPublic, String createdAt, String endDate) {
        this.boardName = boardName;
        this.description = description;
        this.isPublic = isPublic;
        this.createdAt= LocalDateTime.now();
        this.endDate = endDate;
    }

    public void update(String boardName, String description, boolean isPublic) {
        this.boardName = boardName;
        this.description = description;
        this.isPublic = isPublic;
    }
}