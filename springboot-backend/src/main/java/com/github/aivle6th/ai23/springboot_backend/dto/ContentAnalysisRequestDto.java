package com.github.aivle6th.ai23.springboot_backend.dto;

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
    private final String analysisDetail;
    private final String status;
    private final LocalDateTime analyzedAt;


    public  ContentAnalysis toEntity(Post post){
        return ContentAnalysis.builder()
                .contentType(contentType)
                .analysisDetail(this.analysisDetail)
                .status(this.status)
                .analyzedAt(LocalDateTime.now())
                .post(post)
                .build();
    }

}
