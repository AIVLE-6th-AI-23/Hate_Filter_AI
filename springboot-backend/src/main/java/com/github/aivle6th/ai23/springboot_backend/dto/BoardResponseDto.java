package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String contentType;
    private final String filePath;
    private final String status;
    private final Long userId;
    private final String username;
    private final String analysisResult;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.contentType = board.getContentType();
        this.filePath = board.getFilePath();
        this.status = board.getStatus();
        this.userId = board.getUser().getId();
        this.username = board.getUser().getUsername();
        this.analysisResult = board.getAnalysisResult();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
    }
}