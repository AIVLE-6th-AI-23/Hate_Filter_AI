package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.backend.domain.home.dto.HomeRequestDto;
import com.github.aivle6th.ai23.backend.domain.home.dto.HomeResponseDto;
import com.github.aivle6th.ai23.backend.domain.home.entity.Home;
import com.github.aivle6th.ai23.backend.domain.home.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeRepository homeRepository;

    @Transactional
    public Long create(HomeRequestDto request) {
        return homeRepository.save(request.toEntity()).getId();
    }

    @Transactional(readOnly = true)
    public HomeResponseDto findById(Long id) {
        Home home = homeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
        return new HomeResponseDto(home);
    }

    @Transactional(readOnly = true)
    public List<HomeResponseDto> findByStatus(String status) {
        return homeRepository.findByStatus(status).stream()
                .map(HomeResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HomeResponseDto> findByContentType(String contentType) {
        return homeRepository.findByContentType(contentType).stream()
                .map(HomeResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, HomeRequestDto request) {
        Home home = homeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
        home.update(request.getTitle(), request.getContent(), request.getStatus());
    }

    @Transactional
    public void delete(Long id) {
        Home home = homeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
        homeRepository.delete(home);
    }
}