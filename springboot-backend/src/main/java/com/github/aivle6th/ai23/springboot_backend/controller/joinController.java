package com.github.aivle6th.ai23.springboot_backend.controller;

import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class joinController {

    private JoinService joinService;

    @Autowired
    public joinController(JoinService joinService){
        this.joinService = joinService;
    }

    // check join endpoint
    @GetMapping("/jointest")
    @ResponseBody
    public String joinPage(){
        return "Here is Join Page";
    }

    @PostMapping("/join")
    public User createUser(@RequestBody User user){
        return joinService.createUser(user);
    }

}
