package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.Post;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class PostRequestDto {

    private final Long boardId;
    private final String postTitle;
    private final String description;

    // Entity 변환 메서드
    public Post toEntity(Board board, User user) {
        return Post.builder()
                .board(board)
                .user(user)
                .postTitle(postTitle)
                .description(description)
                .build();
    }
}
