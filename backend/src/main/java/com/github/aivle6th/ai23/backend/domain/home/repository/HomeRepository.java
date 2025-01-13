package com.github.aivle6th.ai23.backend.domain.home.repository;

import com.github.aivle6th.ai23.backend.domain.home.entity.Home;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeRepository extends JpaRepository<Home, Long> {
    List<Home> findByStatus(String status);
    List<Home> findByContentType(String contentType);
}