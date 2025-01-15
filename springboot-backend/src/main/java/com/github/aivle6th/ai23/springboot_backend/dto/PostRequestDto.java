package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class PostRequestDto {
    private final Long postId;
    private final Long boardId;
    private final String postTitle;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final Long viewCount;

    // Entity 변환 메서드
    //TODO Mapper로 수정하기
    public Post toEntity() {
        return Post.builder()
                .postId(postId)
                .boardId(boardId)
                .postTitle(postTitle)
                .description(description)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .viewCount(viewCount)
                .build();
    }
}
