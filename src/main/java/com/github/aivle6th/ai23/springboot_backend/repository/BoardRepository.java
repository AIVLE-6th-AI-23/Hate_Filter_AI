package com.github.aivle6th.ai23.springboot_backend.repository;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("""
        SELECT b FROM Board b
        JOIN b.departments d
        WHERE d.id = :deptId
        AND (:cursor IS NULL OR b.createdAt < :cursor)
        ORDER BY b.createdAt DESC
    """)
    List<Board> findBoardsByDepartmentWithCursor(
        @Param("deptId") String deptId,
        @Param("cursor") LocalDateTime cursor,
        Pageable pageable
    );

    @Query("""
        SELECT b FROM Board b
        JOIN b.departments d
        WHERE d.id = :deptId
        AND (:cursor IS NULL OR b.createdAt < :cursor)
        AND b.endDate IS NULL
        ORDER BY b.createdAt DESC
    """)
    List<Board> findActiveBoardsByDepartmentWithCursor(
        @Param("deptId") String deptId,
        @Param("cursor") LocalDateTime cursor,
        Pageable pageable
    );

    @Query("""
        SELECT b FROM Board b
        JOIN b.departments d
        WHERE d.id = :deptId
        AND (:cursor IS NULL OR b.createdAt < :cursor)
        AND b.endDate IS NOT NULL
        ORDER BY b.createdAt DESC
    """)
    List<Board> findCompletedBoardsByDepartmentWithCursor(
        @Param("deptId") String deptId,
        @Param("cursor") LocalDateTime cursor,
        Pageable pageable
    );
}