package com.github.aivle6th.ai23.springboot_backend.repository;

import com.github.aivle6th.ai23.springboot_backend.entity.ContentAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentAnalysisRepository extends JpaRepository<ContentAnalysis,Long> {
    @Query("""
        SELECT ca 
        FROM ContentAnalysis ca 
        LEFT JOIN FETCH ca.analysisCategoryResults acr 
        LEFT JOIN FETCH acr.hateCategory 
        WHERE ca.post.postId = :postId
        ORDER BY ca.analyzedAt DESC
    """)
    Optional<ContentAnalysis> findContentAnalysisWithDetails(Long postId);

    @Query("""
        SELECT ca 
        FROM ContentAnalysis ca 
        LEFT JOIN FETCH ca.analysisCategoryResults acr 
        LEFT JOIN FETCH acr.hateCategory 
        WHERE ca.post.postId = :postId
        ORDER BY ca.analyzedAt DESC
    """)
    List<ContentAnalysis> findContentAnalysisResults(Long postId);
}
