package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.Department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<String> deptIds;

    public static BoardResponseDto from(Board board) {

        return BoardResponseDto.builder()
                .boardId(board.getBoardId())
                .boardTitle(board.getBoardTitle())
                .description(board.getDescription())
                .deptIds(board.getDepartments().stream().map(Department::getDeptId).collect(Collectors.toList()))
                .createdAt(board.getCreatedAt())
                .endDate(board.getEndDate())
                .build();
    }
}