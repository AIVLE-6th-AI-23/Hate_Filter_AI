package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Long boardId;
    private String boardTitle;
    private String description;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime endDate;
    private List<String> deptIds;

    public static BoardDto from(Board board) {
        List<String> deptIds = board.getBoardDepartments().stream()
                .map(boardDepartment -> boardDepartment.getDepartment().getDeptId())
                .toList();

        return BoardDto.builder()
                .boardId(board.getBoardId())
                .boardTitle(board.getBoardTitle())
                .description(board.getDescription())
                .isPublic(board.getIsPublic())
                .createdAt(board.getCreatedAt())
                .endDate(board.getEndDate())
                .deptIds(deptIds)
                .build();
    }
}