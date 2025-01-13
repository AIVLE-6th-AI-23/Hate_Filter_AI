package com.github.aivle6th.ai23.springboot_backend.repository;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByUserId(Long userId);
    List<Board> findByStatus(String status);
    List<Board> findByContentType(String contentType);
}