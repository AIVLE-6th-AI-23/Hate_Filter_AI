package com.github.aivle6th.ai23.springboot_backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.github.aivle6th.ai23.springboot_backend.entity.Post;
import com.github.aivle6th.ai23.springboot_backend.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final PostRepository postRepository;

    public boolean canAccessBoard(Authentication authentication, Long projectId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("BOARD_" + projectId));
    }

    public boolean canAccessPost(Authentication authentication, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Long boardId = post.getBoard().getBoardId();
        return canAccessBoard(authentication, boardId);
    }

}
