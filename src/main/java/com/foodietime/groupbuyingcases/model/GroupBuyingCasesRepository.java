package com.foodietime.groupbuyingcases.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupBuyingCasesRepository extends JpaRepository<GroupBuyingCasesVO, Integer>{

	 // 查詢某會員開的所有團購案（根據 MemberVO.memId）
    List<GroupBuyingCasesVO> findByMember_MemId(Integer memId);
 
 // 查詢某會員開的所有團購案（根據會員ID查詢，並且該會員是團主）
    @Query("SELECT gbc FROM GroupBuyingCasesVO gbc " +
           "JOIN gbc.participants p " +
           "WHERE p.member.memId = :memId AND p.leader = :leader")
    List<GroupBuyingCasesVO> findByMember_MemIdAndLeader(Integer memId, Boolean leader);

    // 查詢某店家開的所有團購案（根據 StoreVO.storId）
    List<GroupBuyingCasesVO> findByStore_StorId(Integer storId);

    // 根據商品編號或商品名稱查詢團購案
    List<GroupBuyingCasesVO> findByGbProd_GbProdIdOrGbProd_GbProdNameContaining(Integer gbProdId, String gbProdName);
}
