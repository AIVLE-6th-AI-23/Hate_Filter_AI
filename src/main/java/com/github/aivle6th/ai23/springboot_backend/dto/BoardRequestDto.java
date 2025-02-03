package com.github.aivle6th.ai23.springboot_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDto {
    private String boardTitle;
    private String description;
    private LocalDateTime endDate;
    private List<String> deptIds;
}
