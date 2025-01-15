package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "POST")
@Builder(toBuilder = true)
@AllArgsConstructor //-> builder를 사용하기 위해서 필요함 모든 값에 대한 생성자가 필요하기 때문에
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
