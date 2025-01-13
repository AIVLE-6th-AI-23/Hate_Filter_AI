package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String contentType;

    private String filePath;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String analysisResult;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Builder
    public Board(String title, String content, String contentType, String filePath,
                 String status, User user) {
        this.title = title;
        this.content = content;
        this.contentType = contentType;
        this.filePath = filePath;
        this.status = status;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String content, String status) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.modifiedAt = LocalDateTime.now();
    }

    public void updateAnalysisResult(String result) {
        this.analysisResult = result;
        this.modifiedAt = LocalDateTime.now();
    }
}