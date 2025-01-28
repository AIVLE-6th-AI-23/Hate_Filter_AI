package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.dto.UserInfoRequestDto;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String signup(UserInfoRequestDto requestDto) {
        if (userRepository.existsByEmployeeId(requestDto.getEmployeeId())) {
            throw new IllegalArgumentException("User already exists");
        }
    
        User newUser = requestDto.toEntity();
        newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(newUser);
        return "User created successfully";
    }

    @Transactional
    public String updateUserInfo(UserInfoRequestDto requestDto) {
        User user = userRepository.findByEmployeeId(requestDto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 필드 업데이트: 요청에 포함된 값만 변경
        if (requestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
        if (requestDto.getEmail() != null) {
            user.setEmail(requestDto.getEmail());
        }

        userRepository.save(user);
        return "User information updated successfully";
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
                .map(user -> {
                    if (user.getDepartment() == null) {
                        throw new IllegalArgumentException("Department not found for user with employeeId: " + employeeId);
                    }
                    return user.getDepartment().getDeptId();
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found with employeeId: " + employeeId));
    }

    public void updateUserActiveStatus(String employeeId, boolean isActive) {
        User user = userRepository.findByEmployeeId(employeeId)
                                .orElseThrow(() -> new EntityNotFoundException("User not found with employeeId : " + employeeId));
        user.setIsActive(isActive);
    }
}

