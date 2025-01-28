package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.BoardCreateRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.BoardResponseDto;
import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.service.BoardService;
import com.github.aivle6th.ai23.springboot_backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "Board API", description = "게시판 관리 API")
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;

    @Operation(summary = "게시판 조회", description = "현재 로그인한 사용자의 부서에 따라 게시판 목록을 조회합니다.")
    @GetMapping("")
    public ResponseEntity<ApiResponseDto<List<BoardResponseDto>>> getBoards(
            @Parameter(description = "페이징 커서 (생성일시 기준)") @RequestParam(name = "cursor", required = false) LocalDateTime cursor,
            @Parameter(description = "페이지 크기 (기본값: 10)") @RequestParam(name = "size", defaultValue = "10") int size,
            @Parameter(description = "게시판 상태 (active, completed, all)") @RequestParam(name = "status", defaultValue = "active") String status
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseDto<>(false, "Unauthorized access", null));
            }

            String employeeId = ((UserDetails) authentication.getPrincipal()).getUsername();
            String deptId = userService.getDeptIdByEmployeeId(employeeId);
            List<Board> boards = "active".equalsIgnoreCase(status)     ? boardService.getActiveBoardsByDepartment(deptId, cursor, size) : 
                                "completed".equalsIgnoreCase(status)  ? boardService.getCompletedBoardsByDepartment(deptId, cursor, size) : 
                                                                        boardService.getBoardsByDepartment(deptId, cursor, size);
            List<BoardResponseDto> boardDtos = boards.stream()
                    .map(BoardResponseDto::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시판 조회 성공", boardDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "서버에서 문제가 발생했습니다.", null));
        }
    }
    

    @Operation(summary = "게시판 생성", description = "새로운 게시판을 생성합니다. 관리자 또는 매니저 권한이 필요합니다.")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponseDto<BoardResponseDto>> createBoard(@RequestBody BoardCreateRequestDto request) {
        try {
            Board savedBoard = boardService.createBoard(request);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시판 생성 성공", BoardResponseDto.from(savedBoard)));
        } catch (Exception e) {
            log.error("게시판 생성 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "게시판 생성 실패: " + e.getMessage(), null));
        }
    }

    @Operation(summary = "게시판 상세 조회", description = "특정 게시판의 상세 정보를 조회합니다.")
    @PreAuthorize("@securityService.canAccessBoard(authentication, #boardId)")
    @GetMapping("/{boardId:\\d+}/detail")
    public ResponseEntity<ApiResponseDto<BoardResponseDto>> getBoardById(@PathVariable Long boardId) {
        Board board = boardService.getBoardById(boardId);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "게시판 조회 성공", BoardResponseDto.from(board)));
    }

    @Operation(summary = "게시판 수정", description = "특정 게시판의 내용을 수정합니다. 관리자 또는 매니저 권한이 필요합니다.")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') and @securityService.canAccessBoard(authentication, #boardId)")
    @PutMapping("/{boardId:\\d+}")
    public ResponseEntity<ApiResponseDto<BoardResponseDto>> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardCreateRequestDto request) {
        Board updatedBoard = boardService.updateBoard(boardId, request);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "게시판 수정 성공", BoardResponseDto.from(updatedBoard)));
    }

    @Operation(summary = "게시판 삭제", description = "특정 게시판을 삭제합니다. 관리자 또는 매니저 권한이 필요합니다.")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') and @securityService.canAccessBoard(authentication, #boardId)")
    @DeleteMapping("/{boardId:\\d+}")
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
