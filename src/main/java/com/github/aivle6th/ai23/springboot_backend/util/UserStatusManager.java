package com.github.aivle6th.ai23.springboot_backend.util;

import org.springframework.stereotype.Component;

import com.github.aivle6th.ai23.springboot_backend.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserStatusManager {
    private final UserService userService;

    public void deactivateUser(String username) {
        userService.updateUserActiveStatus(username, false);
    }

    public void activateUser(String username) {
        userService.updateUserActiveStatus(username, true);
    }
}
