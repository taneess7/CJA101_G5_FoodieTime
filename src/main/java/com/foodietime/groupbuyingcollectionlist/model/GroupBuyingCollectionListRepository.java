package com.foodietime.groupbuyingcollectionlist.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupBuyingCollectionListRepository extends JpaRepository<GroupBuyingCollectionListVO, GroupBuyingCollectionListId> {

	// 查詢單筆收藏（判斷是否已收藏）
    // JpaRepository 已經自動提供了 findById 方法
    // GroupBuyingCollectionListVO findById(GroupBuyingCollectionListId id);

    // 查詢某會員所有收藏
    List<GroupBuyingCollectionListVO> findByMember_MemId(Integer memId);
}
