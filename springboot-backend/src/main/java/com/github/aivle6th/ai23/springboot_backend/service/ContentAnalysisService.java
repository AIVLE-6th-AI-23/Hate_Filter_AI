package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.dto.AnalysisCategoryResultRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.ContentAnalysisResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.ContentAnalysisRequestDto;
import com.github.aivle6th.ai23.springboot_backend.entity.AnalysisCategoryResult;
import com.github.aivle6th.ai23.springboot_backend.entity.ContentAnalysis;
import com.github.aivle6th.ai23.springboot_backend.entity.HateCategory;
import com.github.aivle6th.ai23.springboot_backend.entity.Post;
import com.github.aivle6th.ai23.springboot_backend.repository.ContentAnalysisRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.HateCategoryRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentAnalysisService {

    private final ContentAnalysisRepository contentAnalysisRepository;
    private final HateCategoryRepository hateCategoryRepository;
    private final PostRepository postRepository;

    public ContentAnalysisResponseDto getContentAnalysisWithPost(Long postId) {
        ContentAnalysis contentAnalysis =  contentAnalysisRepository.findContentAnalysisWithDetails(postId)
                .orElseThrow(() -> new RuntimeException("Content Analysis not found"));

        return ContentAnalysisResponseDto.analysisToDto(contentAnalysis);


    }

    public ContentAnalysis createContentAnalysis(ContentAnalysisRequestDto contentAnalysisRequestDto,
                                                 List<AnalysisCategoryResultRequestDto> analysisCategoryResultDtos,
                                                 Long postId) {

        Post post = postRepository.findByPostId(postId);
        ContentAnalysis contentAnalysis = contentAnalysisRequestDto.toEntity(post);

        //분석카테고리 결과 dto에서 categoryId 들을조회
        List<Long> categoryIds = analysisCategoryResultDtos.stream()
                .map(AnalysisCategoryResultRequestDto::getCategoryId)
                .toList();

        // 해당 categoryIds의 hateCateory조회
        List<HateCategory> hateCategories = hateCategoryRepository.findAllByCategoryIdIn(categoryIds);

        // AnalysisCategoryResult 생성
        List<AnalysisCategoryResult> categoryResults = analysisCategoryResultDtos.stream()
                .map(dto -> {
                    HateCategory hateCategory = hateCategories.stream()
                            .filter(hc -> hc.getCategoryId().equals(dto.getCategoryId()))
                            .findFirst()
                            .orElseThrow(() -> new EntityNotFoundException("HateCategory not found for ID: " + dto.getCategoryId()));
                    return dto.toEntity(hateCategory ,contentAnalysis);
                })
                .toList();

        contentAnalysis.setAnalysisCategoryResults(categoryResults);


        //ContentAnalysis 저장
        return contentAnalysisRepository.save(contentAnalysis);
    }
}
