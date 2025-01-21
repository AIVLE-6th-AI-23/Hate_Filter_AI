package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.dto.UserSignupRequestDto;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;
   private final AuthenticationManager authenticationManager;

   @Transactional
public String signup(UserSignupRequestDto requestDto) {
    Optional<User> existingUser = userRepository.findByEmployeeId(requestDto.getEmployeeId());
    
    if (existingUser.isPresent()) {
        User user = existingUser.get();
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);
        return "User updated successfully";
    } else {
        User newUser = requestDto.toEntity();
        newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(newUser);
        return "User created successfully";
    }
}

    /**
     * Employee ID를 기반으로 User의 dept_id를 반환하는 메서드
     * 
     * @param employeeId 직원 ID
     * @return dept_id (String)
     * @throws IllegalArgumentException 직원 ID에 해당하는 유저가 없을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public String getDeptIdByEmployeeId(String employeeId) {
        return userRepository.findByEmployeeId(employeeId)
                .map(user -> user.getDepartment().getDeptId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with employeeId: " + employeeId));
    }

   public User login(String employeeId, String password) {
       try {
            User user = userRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            user.updateLastLogin(LocalDateTime.now());
            return user;
       } catch (AuthenticationException e) {
           return null;
       }
   }
}

