package com.github.aivle6th.ai23.springboot_backend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnalysisStartRequestDTO {
    private final String employeeId;
    private final Long postId;
    private final Long boardId;
    private final String thumbnail;

    public AnalysisStartRequestDTO(PostResponseDto postResponseDto, String employeeId) {
        this.employeeId = employeeId;
        postId = postResponseDto.getPostId();
        boardId = postResponseDto.getBoardId();
        thumbnail = postResponseDto.getThumbnail();
    }
}
