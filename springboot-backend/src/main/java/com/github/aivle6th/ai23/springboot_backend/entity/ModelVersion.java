package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "MODEL_VERSION")
public class ModelVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "model_type")
    private String modelType;

    @Column(name = "version")
    private String version;

    @Column(name = "model_metadata")
    private String modelMetadata;

    @Column(name = "started_at")
    private LocalDateTime startedAt;
}
