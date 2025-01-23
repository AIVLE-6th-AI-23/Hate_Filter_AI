package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.dto.PostRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.PostResponseDto;
import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.Post;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.repository.BoardRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.PostRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    //paging 처리 추가

    /**
     * POST 생성
     * @param postRequestDto
     * @return 생성한 postId 일단 id만, 생성한거 잘 들어갔는지 전부 다 보고 싶으면 post로 내놓기
     */
    public PostResponseDto createPost(PostRequestDto postRequestDto) {

        Board board = boardRepository.findById(postRequestDto.getBoardId())
                .orElseThrow(()-> new EntityNotFoundException("Board not found"));

        User user = userRepository.findById(postRequestDto.getUserId())
                .orElseThrow(()-> new EntityNotFoundException("User not found"));

        // RequestDto -> Entity 변환
        Post post = postRequestDto.toEntity(board, user);

        Post savedPost = postRepository.save(post);
        // Entity -> ResponseDto 변환
        return PostResponseDto.EntityToResponse(savedPost);
    }

    /**
     * 보드 ID에 맞는 POST 목록 전달 함수
     * @param boardId
     * @param cursor
     * @param size
     * @return List<PostResponseDto>
     */
    public List<PostResponseDto> getPostByBoard(Long boardId, LocalDateTime cursor, int size) {
        List<Post> posts = postRepository.findByBoardIdWithCursor(boardId, cursor, PageRequest.of(0, size));
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
        Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));
        return PostResponseDto.EntityToResponse(post); // Entity -> ResponseDto 변환
    }

    /**
     * POST 수정
     * @param postId
     * @param postRequestDto
     * @return
     */
    public PostResponseDto updatePostById(Long postId, PostRequestDto postRequestDto) {
        // 1. 기존 post 찾기
        Post post = postRepository.findById(postId)
                                .orElseThrow(()-> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

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
    // 왜 void 지???
    @Transactional
    public void incrementViewCount(Long postId) {
        postRepository.increaseViewCount(postId);
    }

    /**
     * POST 삭제
     * @param postId
     */
    public void deletePostById(Long postId) {
        // 존재 유무 확인
        if(!postRepository.existsById(postId)){
            throw new EntityNotFoundException("Post ID " + postId + " is not found");
        }
        postRepository.deleteById(postId);
    }
}
