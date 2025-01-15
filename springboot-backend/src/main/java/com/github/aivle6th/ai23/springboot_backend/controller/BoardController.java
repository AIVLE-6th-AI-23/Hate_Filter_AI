package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.dto.BoardDTO;
import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/my")
    public ResponseEntity<List<BoardDTO>> getMyBoards(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<Board> boards = boardService.getUserBoards(userId);
        List<BoardDTO> boardDTOs = boards.stream()
                .map(BoardDTO::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(boardDTOs);
    }

    @GetMapping("/my/page")
    public ResponseEntity<Page<BoardDTO>> getMyBoardsWithPaging(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long userId = Long.parseLong(userDetails.getUsername());
        Page<Board> boardPage = boardService.getUserBoardsWithPaging(userId, pageable);
        Page<BoardDTO> boardDTOPage = boardPage.map(BoardDTO::from);

        return ResponseEntity.ok(boardDTOPage);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDTO> getBoard(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long boardId) {

        Long userId = Long.parseLong(userDetails.getUsername());
        Board board = boardService.getUserBoard(userId, boardId);

        return ResponseEntity.ok(BoardDTO.from(board));
    }
}