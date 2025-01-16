package com.github.aivle6th.ai23.springboot_backend.dto;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.Post;
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
    //TODO Mapper로 수정하기
    public Post toEntity(Board board) {
        return Post.builder()
                .board(board)
                .postTitle(postTitle)
                .description(description)
                .build();
    }
}
