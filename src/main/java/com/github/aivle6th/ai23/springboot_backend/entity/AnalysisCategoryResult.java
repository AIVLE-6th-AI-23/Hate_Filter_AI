package com.github.aivle6th.ai23.springboot_backend.entity;

import java.util.Map;

import com.github.aivle6th.ai23.springboot_backend.util.JsonConverter;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "ANALYSIS_CATEGORY_RESULT")
public class AnalysisCategoryResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @Column(name = "category_score")
    private Float categoryScore;

    @Column(name = "detection_metadata", columnDefinition = "json")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> detectionMetadata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id")
    private ContentAnalysis contentAnalysis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private HateCategory hateCategory;
}