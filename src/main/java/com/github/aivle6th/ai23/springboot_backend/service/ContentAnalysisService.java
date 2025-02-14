package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.dto.AnalysisCategoryResultRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.ContentAnalysisResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.ContentAnalysisRequestDto;
import com.github.aivle6th.ai23.springboot_backend.entity.AnalysisCategoryResult;
import com.github.aivle6th.ai23.springboot_backend.entity.ContentAnalysis;
import com.github.aivle6th.ai23.springboot_backend.entity.HateCategory;
import com.github.aivle6th.ai23.springboot_backend.entity.Post;
import com.github.aivle6th.ai23.springboot_backend.repository.AnalysisCategoryResultRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.ContentAnalysisRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.HateCategoryRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentAnalysisService {

    private final ContentAnalysisRepository contentAnalysisRepository;
    private final HateCategoryRepository hateCategoryRepository;
    private final AnalysisCategoryResultRepository analysisCategoryResultRepository;
    private final PostRepository postRepository;

    public ContentAnalysisResponseDto getContentAnalysisWithPost(Long postId) {
        ContentAnalysis contentAnalysis =  contentAnalysisRepository.findContentAnalysisWithDetails(postId)
                .orElse(new ContentAnalysis());

        return ContentAnalysisResponseDto.analysisToDto(contentAnalysis);
    }

    @Transactional
    public ContentAnalysis createContentAnalysis(ContentAnalysisRequestDto contentAnalysisRequestDto,
                                                 List<AnalysisCategoryResultRequestDto> analysisCategoryResultDtos,
                                                 Long postId) {

        Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));
        ContentAnalysis contentAnalysis = contentAnalysisRequestDto.toEntity(post);

        // 분석카테고리 결과 dto에서 categoryId 들을조회
        List<String> categoryNames = analysisCategoryResultDtos.stream()
                .map(AnalysisCategoryResultRequestDto::getCategoryName)
                .toList();
 
        // 해당 categoryIds의 hateCateory조회
        List<HateCategory> hateCategories = hateCategoryRepository.findAllByCategoryNameIn(categoryNames);

        // AnalysisCategoryResult 생성
        List<AnalysisCategoryResult> categoryResults = analysisCategoryResultDtos.stream()
                .map(dto -> {
                    HateCategory hateCategory = hateCategories.stream()
                            .filter(hc -> hc.getCategoryName().equals(dto.getCategoryName()))
                            .findFirst()
                            .orElseThrow(() -> new EntityNotFoundException("HateCategory not found for Name: " + dto.getCategoryName()));
                    return dto.toEntity(hateCategory ,contentAnalysis);
                })
                .toList();

        contentAnalysis.setAnalysisCategoryResults(categoryResults);

        // AnalysisCategoryResult 저장
        analysisCategoryResultRepository.saveAll(categoryResults);
        //ContentAnalysis 저장
        return contentAnalysisRepository.save(contentAnalysis);
    }
}
