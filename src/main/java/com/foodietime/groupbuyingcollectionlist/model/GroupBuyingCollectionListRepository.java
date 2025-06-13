package com.foodietime.groupbuyingcollectionlist.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GroupBuyingCollectionListRepository extends JpaRepository<GroupBuyingCollectionListVO, GroupBuyingCollectionListId> {

	public void deleteById(Integer gbId, Integer memId);
	
	// 查詢單筆收藏（判斷是否已收藏）
	GroupBuyingCollectionListVO findById_GbIdAndId_MemId(Integer gbId, Integer memId);
	
	// 查詢所有收藏
    List<GroupBuyingCollectionListVO> findAll();

    // 查詢某會員所有收藏
    List<GroupBuyingCollectionListVO> findById_MemId(Integer memId);

    // 刪除單筆收藏（gbId 和 memId）
    void deleteById_GbIdAndId_MemId(Integer gbId, Integer memId);

	
}
