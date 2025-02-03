package com.github.aivle6th.ai23.springboot_backend.repository;

import com.github.aivle6th.ai23.springboot_backend.entity.User;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(@NotEmpty(message = "사원번호는 필수 입력값입니다") String employeeId);
}