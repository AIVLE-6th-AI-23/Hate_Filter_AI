package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponseDto {

    private final Long postId;
    private final Long boardId;
    private final Long userId;
    private final String postTitle;
    private final String description;
    private final String thumbnail;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final Long viewCount;
    private final String status;

    // 엔티티에서 DTO로 변환하는 메서드
    public static PostResponseDto EntityToResponse(Post post) {
        return PostResponseDto.builder()
                .postId(post.getPostId())
                .boardId(post.getBoard().getBoardId())
                .userId(post.getUser().getUserId())
                .postTitle(post.getPostTitle())
                .description(post.getDescription())
                .thumbnail(post.getThumbnail())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .viewCount(post.getViewCount())
                .status(post.getStatus())
                .build();
    }
}
