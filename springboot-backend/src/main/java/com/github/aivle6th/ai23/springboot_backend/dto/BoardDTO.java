package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardDTO {
    private Long boardId;
    private String boardTitle;
    private String description;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime endDate;

    public static BoardDTO from(Board board) {
        return BoardDTO.builder()
                .boardId(board.getBoardId())
                .boardTitle(board.getBoardTitle())
                .description(board.getDescription())
                .isPublic(board.getIsPublic())
                .createdAt(board.getCreatedAt())
                .endDate(board.getEndDate())
                .build();
    }
}