package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.dto.UserDTO;
import com.github.aivle6th.ai23.springboot_backend.entity.Department;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinService {

    private final UserRepository userRepository;

    // don't use DTO
    public User insertUser(User user){
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);
        return userRepository.save(user);
    }

    public User createUser(UserDTO.Post post){
        User user = new User();
        user.setEmployeeId(post.getEmployeeId());
        user.setUserName(post.getUserName());
        user.setEmail(post.getEmail());
        user.setPwd("{noop}" + post.getPwd());
        user.setDeptId(post.getDeptId());

        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);

        return userRepository.save(user);
    }
}
