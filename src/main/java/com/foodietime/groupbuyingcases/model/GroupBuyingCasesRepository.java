package com.foodietime.groupbuyingcases.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupBuyingCasesRepository extends JpaRepository<GroupBuyingCasesVO, Integer>{

	// 查詢某會員參加的所有團購案（根據 MemberVO.memId）
    List<GroupBuyingCasesVO> findByMember_MemId(Integer memId);
 
    // 查詢某會員開設且是團主的團購案（根據會員ID查詢，並且該會員是團主）
    @Query("SELECT gbc FROM GroupBuyingCasesVO gbc " +
           "JOIN gbc.participants p " +
           "WHERE p.member.memId = :memId AND p.leader = :leader")
    List<GroupBuyingCasesVO> findByMember_MemIdAndLeader(Integer memId, byte leader);

    // 查詢某店家開的所有團購案（根據 StoreVO.storId）
    List<GroupBuyingCasesVO> findByStore_StorId(Integer storId);

    // 根據商品編號或商品名稱查詢團購案
    List<GroupBuyingCasesVO> findByGbProd_GbProdIdOrGbProd_GbProdNameContaining(Integer gbProdId, String gbProdName);

    // 根據狀態查詢團購案
    List<GroupBuyingCasesVO> findByGbStatus(Byte gbStatus);
    
    // 根據狀態查詢團購案並按累計購買數量降序排列
    List<GroupBuyingCasesVO> findByGbStatusOrderByCumulativePurchaseQuantityDesc(Byte gbStatus);
    
    // 根據狀態查詢團購案並按創建時間降序排列
    List<GroupBuyingCasesVO> findByGbStatusOrderByGbCreateAtDesc(Byte gbStatus);
    
    // 根據標題關鍵字和狀態搜尋團購案
    List<GroupBuyingCasesVO> findByGbTitleContainingAndGbStatus(String keyword, Byte gbStatus);
}
