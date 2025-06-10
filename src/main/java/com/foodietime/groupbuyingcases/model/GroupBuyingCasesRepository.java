package com.foodietime.groupbuyingcases.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupBuyingCasesRepository extends JpaRepository<GroupBuyingCasesVO, Integer>{

	 // 查詢某會員開的所有團購案（根據 MemberVO.memId）
    List<GroupBuyingCasesVO> findByMember_MemId(Integer memId);

    // 查詢某店家開的所有團購案（根據 StoreVO.storId）
    List<GroupBuyingCasesVO> findByStore_StorId(Integer storId);

    // 查詢某商品對應的團購案（根據 GbprodVO.gbProdId）
    List<GroupBuyingCasesVO> findByGbProd_GbProdId(Integer gbProdId);
}
