package com.github.aivle6th.ai23.springboot_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/")
    public String index() {
        return "index";  // templates/index.html을 반환
    }

//    @GetMapping("/boards")
//    public String boards() {
//        return "boards";  // templates/boards.html을 반환
//    }
}
