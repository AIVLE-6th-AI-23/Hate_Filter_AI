package com.github.aivle6th.ai23.springboot_backend.repository;

import com.github.aivle6th.ai23.springboot_backend.entity.HateCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HateCategoryRepository extends JpaRepository<HateCategory,Long> {

    List<HateCategory> findAllByCategoryIdIn(List<Long> categoryIds);
}
