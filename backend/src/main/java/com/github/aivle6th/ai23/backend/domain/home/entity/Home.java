package com.github.aivle6th.ai23.backend.domain.home.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Home {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String contentType; // TEXT, IMAGE, VIDEO

    private String filePath;

    @Column(nullable = false)
    private String status; // TO_DO, IN_PROGRESS

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Builder
    public Home(String title, String content, String contentType, String filePath, String status) {
        this.title = title;
        this.content = content;
        this.contentType = contentType;
        this.filePath = filePath;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String content, String status) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.modifiedAt = LocalDateTime.now();
    }
}