package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardRequestDto {
    private String title;
    private String content;
    private String contentType;
    private String filePath;
    private String status;

    @Builder
    public BoardRequestDto(String title, String content, String contentType,
                           String filePath, String status) {
        this.title = title;
        this.content = content;
        this.contentType = contentType;
        this.filePath = filePath;
        this.status = status;
    }
}