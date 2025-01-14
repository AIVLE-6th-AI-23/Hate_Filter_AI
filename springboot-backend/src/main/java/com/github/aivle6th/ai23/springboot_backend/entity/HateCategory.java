package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "HATE_CATEGORY")
public class HateCategory {
    @Id
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "description")
    private String description;

    @Column(name = "severity_level")
    private Long severityLevel;

    @OneToMany(mappedBy = "hateCategory")
    private List<AnalysisCategoryResult> analysisResults = new ArrayList<>();
}
