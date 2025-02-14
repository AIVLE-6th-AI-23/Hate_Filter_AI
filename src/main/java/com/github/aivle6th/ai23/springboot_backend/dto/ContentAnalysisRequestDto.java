package com.github.aivle6th.ai23.springboot_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.aivle6th.ai23.springboot_backend.entity.ContentAnalysis;
import com.github.aivle6th.ai23.springboot_backend.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@RequiredArgsConstructor
public class ContentAnalysisRequestDto {

    private final String contentType;
    @JsonProperty("analysisDetail")
    private final String analysisDetail;

    public  ContentAnalysis toEntity(Post post){
        return ContentAnalysis.builder()
                .contentType(contentType)
                .analysisDetail(analysisDetail)
                .analyzedAt(LocalDateTime.now())
                .post(post)
                .build();
    }

}
