package com.github.aivle6th.ai23.springboot_backend.repository;

import com.github.aivle6th.ai23.springboot_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeId(String employeeId);
    Boolean existsByEmployeeId(String employeeId);
    Boolean existsByEmail(String email);
}