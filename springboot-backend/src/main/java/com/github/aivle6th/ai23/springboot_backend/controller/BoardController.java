package com.github.aivle6th.ai23.springboot-backend.controller;

import com.github.aivle6th.ai23.backend.domain.board.dto.BoardRequestDto;
import com.github.aivle6th.ai23.backend.domain.board.dto.BoardResponseDto;
import com.github.aivle6th.ai23.backend.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestParam Long userId,
                                       @RequestBody BoardRequestDto request) {
        return ResponseEntity.ok(boardService.create(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BoardResponseDto>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(boardService.findByUserId(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BoardResponseDto>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(boardService.findByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody BoardRequestDto request) {
        boardService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.delete(id);
        return ResponseEntity.ok().build();
    }
}