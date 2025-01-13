package com.github.aivle6th.ai23.backend.domain.home.dto;

import com.github.aivle6th.ai23.backend.domain.home.entity.Home;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HomeRequestDto {
    private String title;
    private String content;
    private String contentType;
    private String filePath;
    private String status;

    @Builder
    public HomeRequestDto(String title, String content, String contentType, String filePath, String status) {
        this.title = title;
        this.content = content;
        this.contentType = contentType;
        this.filePath = filePath;
        this.status = status;
    }

    public Home toEntity() {
        return Home.builder()
                .title(title)
                .content(content)
                .contentType(contentType)
                .filePath(filePath)
                .status(status)
                .build();
    }
}