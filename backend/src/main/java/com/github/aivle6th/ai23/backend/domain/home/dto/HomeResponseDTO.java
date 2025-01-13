package com.github.aivle6th.ai23.backend.domain.home.dto;

import com.github.aivle6th.ai23.backend.domain.home.entity.Home;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HomeResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String contentType;
    private final String filePath;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public HomeResponseDto(Home home) {
        this.id = home.getId();
        this.title = home.getTitle();
        this.content = home.getContent();
        this.contentType = home.getContentType();
        this.filePath = home.getFilePath();
        this.status = home.getStatus();
        this.createdAt = home.getCreatedAt();
        this.modifiedAt = home.getModifiedAt();
    }
}