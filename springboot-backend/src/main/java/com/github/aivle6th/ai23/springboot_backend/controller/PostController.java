package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.PostRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.PostResponseDto;
import com.github.aivle6th.ai23.springboot_backend.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/{boardId}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 보드 번호에 맞는 POST LIST
    @PreAuthorize("@securityService.canAccessBoard(authentication, #boardId)")
    @GetMapping("/")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getPostByBoardId(
        @PathVariable Long boardId,
        @RequestParam(required = false) LocalDateTime cursor,
        @RequestParam(defaultValue = "10") int size
    ){
        List<PostResponseDto> posts = postService.getPostByBoard(boardId, cursor, size);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 목록 조회 성공",posts));
    }

    // 상세 페이지 보기
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>>details(@PathVariable Long postId){
        PostResponseDto post = postService.getPostById(postId);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 상세페이지 보기 성공", post));
    }

    /**
     * POST 생성 + 추가로 이미지나 영상의 경로가 들어갈 수 있음 주의!
     * @param postRequestDto
     * @return 생성된 POST id
     */
    @PreAuthorize("@securityService.canAccessBoard(authentication, #boardId)")
    @PostMapping("/")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> create(@RequestBody PostRequestDto postRequestDto,
                                                                  @PathVariable Long boardId){
        postRequestDto = postRequestDto.toBuilder()
                .boardId(boardId)
                .build();

        // 로그 추가 (postRequestDto가 boardId를 잘 포함했는지 확인)
        log.info("Updated PostRequestDto with boardId: {}", postRequestDto);

        PostResponseDto createdpost = postService.createPost(postRequestDto);
        return ResponseEntity.ok(new ApiResponseDto<>(true,"Post 생성 성공", createdpost));
    }

    // POST 수정
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>>updatePost(@PathVariable Long postId,
                                                                     @RequestBody PostRequestDto postRequestDto){
        PostResponseDto updatedPost = postService.updatePostById(postId, postRequestDto);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 수정 성공", updatedPost));
    }


    // POST 조회수 UPDATE
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<Void>>increaseViewCount(@PathVariable Long postId){
        postService.incrementViewCount(postId);
        return ResponseEntity.ok(new ApiResponseDto<>(true , "Post 조회수 업데이트 성공", null));
    }

    // POST 삭제
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<Void>>deletePost(@PathVariable Long postId){
        postService.deletePostById(postId);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 삭제 성공",null));
    }
}

