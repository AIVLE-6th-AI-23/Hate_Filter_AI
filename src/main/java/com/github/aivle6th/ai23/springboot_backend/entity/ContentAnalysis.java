package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "CONTENT_ANALYSIS")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ContentAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id")
    private Long analysisId;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "analysis_detail", columnDefinition = "TEXT")
    private String analysisDetail;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @OneToMany(mappedBy = "contentAnalysis" ,cascade = CascadeType.ALL)
    @Builder.Default
    private List<AnalysisCategoryResult> analysisCategoryResults = new ArrayList<>();

}
