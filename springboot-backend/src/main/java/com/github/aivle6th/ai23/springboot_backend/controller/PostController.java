package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.PostRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.PostResponseDto;
import com.github.aivle6th.ai23.springboot_backend.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Post API", description = "게시물 관리 API")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시판의 게시물 목록 조회", description = "특정 게시판에 등록된 게시물 목록을 조회합니다.")
    @PreAuthorize("@securityService.canAccessBoard(authentication, #boardId)")
    @GetMapping("")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getPostByBoardId(
            @Parameter(description = "게시판 ID", required = true) @PathVariable Long boardId,
            @Parameter(description = "페이징 커서 (등록일 기준)") @RequestParam(required = false) LocalDateTime cursor,
            @Parameter(description = "페이지 크기 (기본값: 10)") @RequestParam(defaultValue = "10") int size
    ) {
        List<PostResponseDto> posts = postService.getPostByBoard(boardId, cursor, size);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 목록 조회 성공", posts));
    }

    @Operation(summary = "게시물 상세 조회", description = "특정 게시물의 상세 정보를 조회합니다.")
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> details(
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId) {
        PostResponseDto post = postService.getPostById(postId);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 상세페이지 보기 성공", post));
    }

    @Operation(summary = "게시물 생성", description = "특정 게시판에 새로운 게시물을 생성합니다.")
    @PreAuthorize("@securityService.canAccessBoard(authentication, #boardId)")
    @PostMapping("/create")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> create(
            @RequestBody PostRequestDto postRequestDto,
            @Parameter(description = "게시판 ID", required = true) @PathVariable Long boardId) {
        postRequestDto = postRequestDto.toBuilder()
                .boardId(boardId)
                .build();

        log.info("Updated PostRequestDto with boardId: {}", postRequestDto);

        PostResponseDto createdPost = postService.createPost(postRequestDto);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 생성 성공", createdPost));
    }

    @Operation(summary = "게시물 수정", description = "특정 게시물의 내용을 수정합니다.")
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> updatePost(
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId,
            @RequestBody PostRequestDto postRequestDto) {
        PostResponseDto updatedPost = postService.updatePostById(postId, postRequestDto);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 수정 성공", updatedPost));
    }

    @Operation(summary = "게시물 조회수 증가", description = "특정 게시물의 조회수를 증가시킵니다.")
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<Void>> increaseViewCount(
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId) {
        postService.incrementViewCount(postId);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 조회수 업데이트 성공", null));
    }

    @Operation(summary = "게시물 삭제", description = "특정 게시물을 삭제합니다.")
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<Void>> deletePost(
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId) {
        postService.deletePostById(postId);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 삭제 성공", null));
    }
}

