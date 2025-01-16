package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class BoardDTO {
    private Long boardId;
    private String boardTitle;
    private String description;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime endDate;

    public static BoardDTO from(Board board) {
        return new BoardDTO(
                board.getBoardId(),
                board.getBoardTitle(),
                board.getDescription(),
                board.getIsPublic(),
                board.getCreatedAt(),
                board.getEndDate()
        );
    }
}