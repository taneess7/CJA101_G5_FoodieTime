package com.foodietime.postcategory.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategoryVO, Integer>{

	// 根據分類名稱查詢，用於檢查唯一性
		Optional<PostCategoryVO> findByPostCate(String postCate);
}
