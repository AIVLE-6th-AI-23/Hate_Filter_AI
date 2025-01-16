package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.dto.PostRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.PostResponseDto;
import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.Post;
import com.github.aivle6th.ai23.springboot_backend.repository.BoardRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    /**
     * 보드 ID에 맞는 POST 목록 전달 함수
     * @param boardId
     * @return List<PostResponseDto>
     */
    public List<PostResponseDto> getPostByBoard(Long boardId) {
        List<Post> posts = postRepository.findByBoard_boardId(boardId);
        return posts.stream()
                .map(PostResponseDto::EntityToResponse)
                .collect(Collectors.toList());
    }

    /**
     * POST 상세 페이지
     * @param postId
     * @return PostResponseDto
     */
    public PostResponseDto getPostById(Long postId) {
        Post post = postRepository.findByPostId(postId);
        return PostResponseDto.EntityToResponse(post); // Entity -> ResponseDto 변환
    }

    /**
     * POST 생성
     * @param postRequestDto
     * @return 생성한 postId 일단 id만, 생성한거 잘 들어갔는지 전부 다 보고 싶으면 post로 내놓기
     */
    public Long createPost(PostRequestDto postRequestDto) {

        Board board = boardRepository.findById(postRequestDto.getBoardId())
                .orElseThrow(()-> new EntityNotFoundException("Board not found"));
        // RequestDto -> Entity 변환
        Post post = postRequestDto.toEntity(board);

        Post savedPost = postRepository.save(post);
        return savedPost.getPostId();
    }

    /**
     * POST 수정
     * @param postId
     * @param postRequestDto
     * @return
     */
    public PostResponseDto updatePostById(Long postId, PostRequestDto postRequestDto) {
        // 1. 기존 post 찾기
        Post post = postRepository.findById(postId).orElseThrow(()-> new EntityNotFoundException("Post not found"));

        Post updatedPost = post.toBuilder()
                .postTitle(postRequestDto.getPostTitle())
                .description(postRequestDto.getDescription())
                .build();

        Post savedPost = postRepository.save(updatedPost);
        return PostResponseDto.EntityToResponse(savedPost);
    }

    /**
     * VIEW COUNT 증가
     * @param postId
     * @return
     */
    @Transactional
    public void incrementViewCount(Long postId) {
        postRepository.increaseViewCount(postId);
    }
}
