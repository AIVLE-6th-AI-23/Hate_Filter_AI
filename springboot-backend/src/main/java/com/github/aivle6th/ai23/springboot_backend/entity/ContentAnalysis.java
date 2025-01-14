package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "CONTENT_ANALYSIS")
public class ContentAnalysis {
    @Id
    @Column(name = "analysis_id")
    private Long analysisId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "analysis_detail")
    private String analysisDetail;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @Column(name = "status")
    private String status;

    @OneToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    @OneToMany(mappedBy = "contentAnalysis")
    private List<AnalysisCategoryResult> categoryResults = new ArrayList<>();
}
