package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "POST")
public class Post {
    @Id
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "post_title")
    private String postTitle;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "dept_id")
    private Long viewCount;

    @ManyToOne
    @JoinColumn(name = "board_id", insertable = false, updatable = false)
    private Board board;

    @OneToOne(mappedBy = "post")
    private ContentAnalysis contentAnalysis;
}
