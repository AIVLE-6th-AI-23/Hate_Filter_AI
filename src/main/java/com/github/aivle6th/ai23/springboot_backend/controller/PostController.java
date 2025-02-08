package com.github.aivle6th.ai23.springboot_backend.controller;

import com.beust.jcommander.internal.Nullable;
import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.PostRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.PostResponseDto;
import com.github.aivle6th.ai23.springboot_backend.service.BlobStorageService;
import com.github.aivle6th.ai23.springboot_backend.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/{boardId:\\d+}/posts")
@RequiredArgsConstructor
@Tag(name = "Post API", description = "게시물 관리 API")
public class PostController {

    private final PostService postService;
    private final BlobStorageService blobStorageService;

    @Operation(summary = "게시판의 게시물 목록 조회", description = "특정 게시판에 등록된 게시물 목록을 조회합니다.")
    @PreAuthorize("@securityService.canAccessBoard(authentication, #boardId)")
    @GetMapping("")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getPostByBoardId(
            @Parameter(description = "게시판 ID", required = true) @PathVariable Long boardId,
            @Parameter(description = "페이징 커서 (등록일 기준)") @RequestParam(name = "cursor", required = false) LocalDateTime cursor,
            @Parameter(description = "페이지 크기 (기본값: 10)") @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            List<PostResponseDto> posts = postService.getPostByBoard(boardId, cursor, size);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 목록 조회 성공", posts));
        } catch (Exception e) {
            log.error("Post 목록 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "Post 목록 조회 중 문제가 발생했습니다.", null));
            
        } 
    }

    @Operation(summary = "게시물 상세 조회", description = "특정 게시물의 상세 정보를 조회합니다.")
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @GetMapping("/{postId:\\d+}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> details(
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId) {
        try {
            PostResponseDto post = postService.getPostById(postId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 상세페이지 보기 성공", post));
        } catch (Exception e) {
            log.error("Post 상세페이지 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "Post 상세페이지 조회 중 문제가 발생했습니다.", null));
        } 
    }

    @Operation(summary = "게시물 생성", description = "특정 게시판에 새로운 게시물을 생성합니다.")
    @PreAuthorize("@securityService.canAccessBoard(authentication, #boardId)")
    @PostMapping("/create")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> create(
            @RequestBody PostRequestDto postRequestDto,
            @Parameter(description = "게시판 ID", required = true) @PathVariable Long boardId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseDto<>(false, "Unauthorized access", null));
            }

            String employeeId = ((UserDetails) authentication.getPrincipal()).getUsername();
            
            postRequestDto = postRequestDto.toBuilder()
                    .boardId(boardId)
                    .build();

            log.info("Updated PostRequestDto with boardId: {}", postRequestDto);

            PostResponseDto createdPost = postService.createPost(employeeId, postRequestDto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 생성 성공", createdPost));
        } catch (Exception e) {
            log.error("Post 생성 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "Post 생성 중 문제가 발생했습니다.", null));
        } 
    }

    @Operation(summary = "게시물 수정", description = "특정 게시물의 내용을 수정합니다.")
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @PutMapping("/{postId:\\d+}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> updatePost(
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId,
            @RequestBody PostRequestDto postRequestDto) {
        try {
            PostResponseDto updatedPost = postService.updatePostById(postId, postRequestDto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 수정 성공", updatedPost));
        } catch (Exception e) {
            log.error("Post 수정 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "Post 수정 중 문제가 발생했습니다.", null));
        } 
    }

    @Operation(summary = "게시물 조회수 증가", description = "특정 게시물의 조회수를 증가시킵니다.")
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @PatchMapping("/{postId:\\d+}/view")
    public ResponseEntity<ApiResponseDto<Void>> increaseViewCount(
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId) {
        try {
            postService.incrementViewCount(postId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 조회수 업데이트 성공", null));
        } catch (Exception e) {
            log.error("Post 조회수 업데이트 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "Post 조회수 업데이트 중 문제가 발생했습니다.", null));
        } 
    }

    @Operation(summary = "게시물 상태 업데이트", description = "특정 게시물의 상태를 업데이트 합니다.")
    @PatchMapping("/{postId:\\d+}/status/{status}")
    public ResponseEntity<ApiResponseDto<Void>> updateStatus(
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId,
            @Parameter(description = "상태", required = false) @Nullable @PathVariable String status) {
        try {
            postService.updateStatus(postId, status);;
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 상태 업데이트 성공", null));
        } catch (Exception e) {
            log.error("Post 상태 업데이트 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "Post 상태 업데이트 중 문제가 발생했습니다.", null));
        } 
    }

    @Operation(summary = "파일 업로드", description = "특정 게시물에 대한 파일을 업로드 및 Url을 저장 합니다.")
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @PostMapping(value = "/{postId:\\d+}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<Void>> uploadFile(
        @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId,
        @Parameter(description = "업로드할 파일", required = true)
        @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = blobStorageService.uploadFile(file, postId);
            postService.updateThumbnail(postId, fileUrl);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "파일 업로드 및 url 저장 성공", null));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(new ApiResponseDto<>(false, "파일 업로드 및 url 저장 실패", null));
        } catch (Exception e) {
            log.error("파일 업로드 및 url 저장 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "파일 업로드 및 url 저장 중 문제가 발생했습니다.", null));
        } 
    }

    @Operation(summary = "게시물 삭제", description = "특정 게시물을 삭제합니다.")
    @PreAuthorize("@securityService.canAccessPost(authentication, #postId)")
    @DeleteMapping("/{postId:\\d+}")
    public ResponseEntity<ApiResponseDto<Void>> deletePost(
            @Parameter(description = "게시물 ID", required = true) @PathVariable Long postId) {
        try {
            postService.deletePostById(postId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Post 삭제 성공", null));
        } catch (Exception e) {
            log.error("게시물 삭제 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "게시물 삭제 중 문제가 발생했습니다.", null));
        } 
    }
}

