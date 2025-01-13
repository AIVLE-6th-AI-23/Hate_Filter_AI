package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.dto.UserRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.UserResponseDto;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long create(UserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
        return userRepository.save(request.toEntity()).getId();
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return new UserResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
        return new UserResponseDto(user);
    }

    @Transactional
    public void update(Long id, UserRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.update(request.getPassword(), request.getUsername());
    }
}