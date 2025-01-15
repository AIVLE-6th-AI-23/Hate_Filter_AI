package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.exception.BoardNotFoundException;
import com.github.aivle6th.ai23.springboot_backend.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public List<Board> getUserBoards(Long userId) {
        return boardRepository.findAllBoardsByUserId(userId);
    }

    public Page<Board> getUserBoardsWithPaging(Long userId, Pageable pageable) {
        return boardRepository.findAllBoardsByUserIdWithPaging(userId, pageable);
    }

    public Board getUserBoard(Long userId, Long boardId) {
        return boardRepository.findBoardByUserIdAndBoardId(userId, boardId)
                .orElseThrow(() -> new BoardNotFoundException("접근할 수 없는 게시판입니다."));
    }
}