package com.github.aivle6th.ai23.backend.domain.home.controller;

import com.github.aivle6th.ai23.backend.domain.home.dto.HomeRequestDto;
import com.github.aivle6th.ai23.backend.domain.home.dto.HomeResponseDto;
import com.github.aivle6th.ai23.backend.domain.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody HomeRequestDto request) {
        return ResponseEntity.ok(homeService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomeResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(homeService.findById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<HomeResponseDto>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(homeService.findByStatus(status));
    }

    @GetMapping("/type/{contentType}")
    public ResponseEntity<List<HomeResponseDto>> findByContentType(@PathVariable String contentType) {
        return ResponseEntity.ok(homeService.findByContentType(contentType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody HomeRequestDto request) {
        homeService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        homeService.delete(id);
        return ResponseEntity.ok().build();
    }
}