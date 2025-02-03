package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {
    private Long boardId;
    private String boardTitle;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime endDate;

    public static BoardResponseDto from(Board board) {

        return BoardResponseDto.builder()
                .boardId(board.getBoardId())
                .boardTitle(board.getBoardTitle())
                .description(board.getDescription())
                .createdAt(board.getCreatedAt())
                .endDate(board.getEndDate())
                .build();
    }
}