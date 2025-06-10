package com.foodietime.grouppurchasereport.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupPurchaseReportRepository extends JpaRepository<GroupPurchaseReportVO, Integer> {

    // 查詢特定會員的檢舉紀錄（根據 MemberVO 中的 memId）
    List<GroupPurchaseReportVO> findByMember_MemId(Integer memId);

    // 查詢特定團購案的檢舉紀錄（根據 GroupBuyingCasesVO 中的 gbId）
    List<GroupPurchaseReportVO> findByGroupBuyingCase_GbId(Integer gbId);
}
