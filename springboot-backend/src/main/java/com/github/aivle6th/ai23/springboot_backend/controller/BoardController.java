package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.dto.BoardDTO;
import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.CustomUserDetails;
import com.github.aivle6th.ai23.springboot_backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/boards")
    public String listBoards(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String deptId = userDetails.getUser().getDepartment().getDeptId();
        List<Board> boards = boardService.getBoardsByDepartment(deptId);
        model.addAttribute("boards", boards);
        return "boards";
    }
}