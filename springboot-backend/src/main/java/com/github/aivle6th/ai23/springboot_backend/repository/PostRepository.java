package com.github.aivle6th.ai23.springboot_backend.repository;

import com.github.aivle6th.ai23.springboot_backend.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 해당 boardId를 가진 Board에 포함된 Posts
    @Query("""
        SELECT p FROM Post p
        WHERE p.board.boardId = :boardId
        AND (:cursor IS NULL OR p.createdAt < :cursor)
        ORDER BY p.createdAt DESC           
    """)
    List<Post> findByBoardIdWithCursor(
        @Param("boardId") Long boardId, 
        @Param("cursor") LocalDateTime cursor,
        Pageable pageable);

    // 조회수 +1 증가 로직
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.postId = :postId")
    void increaseViewCount(@Param("postId") Long postId);

}
