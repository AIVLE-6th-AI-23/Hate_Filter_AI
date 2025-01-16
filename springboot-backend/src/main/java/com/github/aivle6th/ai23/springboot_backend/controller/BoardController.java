package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponse;
import com.github.aivle6th.ai23.springboot_backend.dto.BoardCreateRequest;
import com.github.aivle6th.ai23.springboot_backend.dto.BoardDTO;
import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/boards/{deptId}")
    public ResponseEntity<ApiResponse<List<BoardDTO>>> getBoardsByDepartment(@PathVariable String deptId) {
        List<Board> boards = boardService.getBoardsByDepartment(deptId);
        List<BoardDTO> boardDTOs = boards.stream()
                .map(BoardDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "게시판 조회 성공", boardDTOs));
    }

    @PostMapping("/boards/create")
    public ResponseEntity<ApiResponse<BoardDTO>> createBoard(@RequestBody BoardCreateRequest request) {
        try {
            Board savedBoard = boardService.createBoard(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "게시판 생성 성공", BoardDTO.from(savedBoard)));
        } catch (Exception e) {
            log.error("게시판 생성 실패: ", e);  // 로그 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "게시판 생성 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("/boards/detail/{boardId}")
    public ResponseEntity<ApiResponse<BoardDTO>> getBoardById(@PathVariable Long boardId) {
        Board board = boardService.getBoardById(boardId);
        return ResponseEntity.ok(new ApiResponse<>(true, "게시판 조회 성공", BoardDTO.from(board)));
    }

    @PutMapping("/boards/update/{boardId}")
    public ResponseEntity<ApiResponse<BoardDTO>> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardCreateRequest request) {
        Board updatedBoard = boardService.updateBoard(boardId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "게시판 수정 성공", BoardDTO.from(updatedBoard)));
    }

    @DeleteMapping("/boards/delete/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok(new ApiResponse<>(true, "게시판 삭제 성공", null));
    }

    @GetMapping("/boards")
    public ResponseEntity<ApiResponse<List<BoardDTO>>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        List<BoardDTO> boardDTOs = boards.stream()
                .map(BoardDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "전체 게시판 조회 성공", boardDTOs));
    }
}
