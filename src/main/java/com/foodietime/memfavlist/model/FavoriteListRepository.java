package com.foodietime.memfavlist.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteListRepository extends JpaRepository<FavoriteListVO, FavoriteListId>{
	
	//根據複合主鍵查詢
	FavoriteListVO findByIdMemIdAndIdProdId(Integer memId, Integer prodId);

    // 刪除單一收藏
    void deleteByIdMemIdAndIdProdId(Integer memId, Integer prodId);

    // 刪除某會員的所有收藏
    void deleteAllByIdMemId(Integer memId);

    // 取得某會員所有收藏
    List<FavoriteListVO> findAllByIdMemId(Integer memId);

}
