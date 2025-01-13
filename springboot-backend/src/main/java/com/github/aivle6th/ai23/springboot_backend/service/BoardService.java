package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.dto.BoardRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.BoardResponseDto;
import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.repository.BoardRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long create(Long userId, BoardRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return boardRepository.save(request.toEntity(user)).getId();
    }

    @Transactional(readOnly = true)
    public BoardResponseDto findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Board not found: " + id));
        return new BoardResponseDto(board);
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDto> findByUserId(Long userId) {
        return boardRepository.findByUserId(userId).stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDto> findByStatus(String status) {
        return boardRepository.findByStatus(status).stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, BoardRequestDto request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Board not found: " + id));
        board.update(request.getTitle(), request.getContent(), request.getStatus());
    }

    @Transactional
    public void delete(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Board not found: " + id));
        boardRepository.delete(board);
    }
}