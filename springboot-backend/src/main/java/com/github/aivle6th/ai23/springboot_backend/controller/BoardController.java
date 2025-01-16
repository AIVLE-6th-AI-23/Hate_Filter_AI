package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponse;
import com.github.aivle6th.ai23.springboot_backend.dto.BoardDTO;
import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.CustomUserDetails;
import com.github.aivle6th.ai23.springboot_backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/boards/{deptId}")
    public ResponseEntity<ApiResponse<List<BoardDTO>>> getBoardsByDepartment(@PathVariable String deptId) {
        List<Board> boards = boardService.getBoardsByDepartment(deptId);
        List<BoardDTO> boardDTOs = boards.stream()
                .map(board -> new BoardDTO(
                        board.getBoardId(),
                        board.getBoardTitle(),
                        board.getDescription(),
                        board.getIsPublic(),
                        board.getCreatedAt(),
                        board.getEndDate()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "게시판 조회 성공", boardDTOs));
    }
}
