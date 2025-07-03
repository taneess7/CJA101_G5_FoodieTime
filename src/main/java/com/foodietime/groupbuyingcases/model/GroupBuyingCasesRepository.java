package com.foodietime.groupbuyingcases.model;

import java.util.List;
import java.util.Optional;

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

 // 查詢某會員開設且是團主的團購案，並根據狀態篩選
    @Query("SELECT gbc FROM GroupBuyingCasesVO gbc " +
           "JOIN gbc.participants p " +
           "WHERE p.member.memId = :memId AND p.leader = :leader AND gbc.gbStatus = :gbStatus")
    List<GroupBuyingCasesVO> findByMember_MemIdAndLeaderAndGbStatus(Integer memId, byte leader, byte gbStatus);
    
    // JOIN participants 表，按 member.memId AND participants.leader 筛选
    List<GroupBuyingCasesVO> findDistinctByParticipants_Member_MemIdAndParticipants_Leader(
        Integer memId,   // 同 MemberVO.memId
        Byte leader      // 0＝團主、1＝非團主
      );
    
    //依團購編號(gbId)與會員編號(memId)查詢單筆團購，確保只能看到自己的紀錄
    Optional<GroupBuyingCasesVO> findByGbIdAndMember_MemId(Integer gbId,Integer memId);

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

    List<GroupBuyingCasesVO> findByGbStatusAndSettled(Byte gbStatus, Boolean settled);

    // 查詢所有已到期且狀態為招募中的團購案
    @Query("SELECT gbc FROM GroupBuyingCasesVO gbc WHERE gbc.gbStatus = 1 AND gbc.gbEndTime <= CURRENT_TIMESTAMP")
    List<GroupBuyingCasesVO> findAllEndedRecruiting();
}
