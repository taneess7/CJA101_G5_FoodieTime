package com.foodietime.memfavlist.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteListRepository extends JpaRepository<FavoriteListVO, FavoriteListId>{

	// 查詢某會員所有收藏
    List<FavoriteListVO> findAllByMemId(Integer memId);
    
//	//根據複合主鍵查詢
//	FavoriteListVO findByMemIdAndProdId(Integer memId, Integer prodId);
//
//    // 刪除單一收藏
//    void deleteByMemIdAndProdId(Integer memId, Integer prodId);
//
//    // 刪除某會員的所有收藏
//    void deleteAllByMemId(Integer memId);
//
    
//
}
