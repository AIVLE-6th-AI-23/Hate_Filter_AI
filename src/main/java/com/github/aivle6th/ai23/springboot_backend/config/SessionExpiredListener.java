package com.github.aivle6th.ai23.springboot_backend.config;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.github.aivle6th.ai23.springboot_backend.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionExpiredListener implements ApplicationListener<SessionDestroyedEvent> {

    private final UserService userService;

    @Override
    @Transactional
    public void onApplicationEvent(SessionDestroyedEvent event) {
        event.getSecurityContexts().forEach(securityContext -> {
            String employeeId = securityContext.getAuthentication().getName();
            userService.updateUserActiveStatus(employeeId, false);
        });
    }
}