package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.dto.PostRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.PostResponseDto;
import com.github.aivle6th.ai23.springboot_backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{boardId}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 보드 번호에 맞는 POST LIST
    @GetMapping("/")
    public ResponseEntity<List<PostResponseDto>> getPostByBoardId(@PathVariable Long boardId){
        List<PostResponseDto> posts = postService.getPostByBoard(boardId);
        //if(posts.isEmpty()) return ResponseEntity.notFound().build(); 없을 때는 어떻게 처리하지?
        return ResponseEntity.ok(posts);
    }

    // 상세 페이지 보기
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto>details(@PathVariable Long postId){
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    /**
     * POST 생성 + 추가로 이미지나 영상의 경로가 들어갈 수 있음 주의!
     * @param postRequestDto
     * @return 생성된 POST id
     */
    @PostMapping("/")
    public ResponseEntity<Long> create(@RequestBody PostRequestDto postRequestDto){
        return ResponseEntity.ok(postService.createPost(postRequestDto));
    }

    // POST 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto>updatePost(@PathVariable Long postId,
                                                     @RequestBody PostRequestDto postRequestDto){
        return ResponseEntity.ok(postService.updatePostById(postId, postRequestDto));
    }


    // POST 조회수 UPDATE
    @PatchMapping("/")
    public ResponseEntity<Void>increaseViewCount(@PathVariable Long postId){
        postService.incrementViewCount(postId);
        return ResponseEntity.ok().build();
    }
    // POST 삭제?
}

