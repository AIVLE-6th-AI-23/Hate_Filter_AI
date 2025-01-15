package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ANALYSIS_CATEGORY_RESULT")
public class AnalysisCategoryResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @Column(name = "analysis_id")
    private Long analysisId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_score")
    private Float categoryScore;

    @Column(name = "detection_metadata")
    private String detectionMetadata;

    @ManyToOne
    @JoinColumn(name = "analysis_id", insertable = false, updatable = false)
    private ContentAnalysis contentAnalysis;

    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private HateCategory hateCategory;
}