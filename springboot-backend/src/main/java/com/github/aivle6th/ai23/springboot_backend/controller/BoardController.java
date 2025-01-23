package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.BoardCreateRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.BoardResponseDto;
import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.service.BoardService;
import com.github.aivle6th.ai23.springboot_backend.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;

    @GetMapping("") // 수정 필 - 쿠키에서 받아와서 처리 - 현재 유저의 부서에 따른 조회...
    public ResponseEntity<ApiResponseDto<List<BoardResponseDto>>> getBoards(
        @PathVariable String employeeId,
        @RequestParam(required = false) LocalDateTime cursor,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "active") String status
    ) {
        String deptId = userService.getDeptIdByEmployeeId(employeeId);
        List<Board> boards = "active".equalsIgnoreCase(status)     ?  boardService.getActiveBoardsByDepartment(deptId, cursor, size) : 
                             "completed".equalsIgnoreCase(status)  ?  boardService.getCompletedBoardsByDepartment(deptId, cursor, size) : 
                                                                      boardService.getBoardsByDepartment(deptId, cursor, size);
        List<BoardResponseDto> boardDtos = boards.stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponseDto<>(true, "게시판 조회 성공", boardDtos));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponseDto<BoardResponseDto>> createBoard(@RequestBody BoardCreateRequestDto request) {
        try {
            Board savedBoard = boardService.createBoard(request);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시판 생성 성공", BoardResponseDto.from(savedBoard)));
        } catch (Exception e) {
            log.error("게시판 생성 실패: ", e);  // 로그 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "게시판 생성 실패: " + e.getMessage(), null));
        }
    }

    @PreAuthorize("@securityService.canAccessBoard(authentication, #boardId)")
    @GetMapping("/{boardId}/detail")
    public ResponseEntity<ApiResponseDto<BoardResponseDto>> getBoardById(@PathVariable Long boardId) {
        Board board = boardService.getBoardById(boardId);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "게시판 조회 성공", BoardResponseDto.from(board)));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') and @securityService.canAccessBoard(authentication, #boardId)")
    @PutMapping("/{boardId}")
    public ResponseEntity<ApiResponseDto<BoardResponseDto>> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardCreateRequestDto request) {
        Board updatedBoard = boardService.updateBoard(boardId, request);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "게시판 수정 성공", BoardResponseDto.from(updatedBoard)));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') and @securityService.canAccessBoard(authentication, #boardId)")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "게시판 삭제 성공", null));
    }

    // @GetMapping("/all")
    // public ResponseEntity<ApiResponseDto<List<BoardDto>>> getAllBoards() {
    //     List<Board> boards = boardService.getAllBoards();
    //     List<BoardDto> boardDtos = boards.stream()
    //             .map(BoardDto::from)
    //             .collect(Collectors.toList());
    //     return ResponseEntity.ok(new ApiResponseDto<>(true, "전체 게시판 조회 성공", boardDtos));
    // }
}
