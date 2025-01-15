package com.github.aivle6th.ai23.springboot_backend.repository;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT DISTINCT b FROM Board b " +
            "JOIN BoardDepartment bd ON b.id = bd.boardId " +
            "JOIN Department d ON bd.departmentId = d.id " +
            "JOIN User u ON d.id = u.departmentId " +
            "WHERE u.id = :userId")
    List<Board> findAllBoardsByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT b FROM Board b " +
            "JOIN BoardDepartment bd ON b.id = bd.boardId " +
            "JOIN Department d ON bd.departmentId = d.id " +
            "JOIN User u ON d.id = u.departmentId " +
            "WHERE u.id = :userId")
    Page<Board> findAllBoardsByUserIdWithPaging(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "JOIN BoardDepartment bd ON b.id = bd.boardId " +
            "JOIN Department d ON bd.departmentId = d.id " +
            "JOIN User u ON d.id = u.departmentId " +
            "WHERE u.id = :userId AND b.id = :boardId")
    Optional<Board> findBoardByUserIdAndBoardId(
            @Param("userId") Long userId,
            @Param("boardId") Long boardId);
}