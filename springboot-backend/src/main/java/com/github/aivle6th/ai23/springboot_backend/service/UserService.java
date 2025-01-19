//package com.github.aivle6th.ai23.springboot_backend.service;
//
//import com.github.aivle6th.ai23.springboot_backend.entity.User;
//import com.github.aivle6th.ai23.springboot_backend.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final AuthenticationManager authenticationManager;
//
//    @Transactional
//    public User signup(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPwd()));
//        user.setIsActive(true);
//        user.setCreatedAt(LocalDateTime.now());
//        return userRepository.save(user);
//    }
//
//    public String login(String employeeId, String password) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(employeeId, password)
//            );
//
//            if (authentication.isAuthenticated()) {
//                User user = userRepository.findByEmployeeId(employeeId)
//                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//                user.updateLastLogin(LocalDateTime.now());
//                return "Login successful";
//            }
//
//            return "Authentication failed";
//        } catch (AuthenticationException e) {
//            return "Invalid credentials";
//        }
//    }
//}
// 희현이 거 test를 위해 주석처리

