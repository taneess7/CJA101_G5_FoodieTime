package com.foodietime.grouppurchasereport.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GroupPurchaseReportRepository extends JpaRepository<GroupPurchaseReportVO, Integer> {

    // 查詢特定會員的所有檢舉紀錄（根據 MemberVO 中的 memId）
    List<GroupPurchaseReportVO> findByMember_MemId(Integer memId);

    // 查詢特定團購案的檢舉紀錄（根據 GroupBuyingCasesVO 中的 gbId）
    List<GroupPurchaseReportVO> findByGroupBuyingCase_GbId(Integer gbId);

    // 查詢檢舉狀態為指定狀態的檢舉紀錄 (根據 reportStatus)
    List<GroupPurchaseReportVO> findByReportStatus(Byte reportStatus);

    // 查詢某會員對某一團購案的檢舉紀錄（通過 memId 和 gbId）
    List<GroupPurchaseReportVO> findByMember_MemIdAndGroupBuyingCase_GbId(Integer memId, Integer gbId);

    // 檢查某會員是否對某團購案件進行過檢舉
    boolean existsByMember_MemIdAndGroupBuyingCase_GbId(Integer memId, Integer gbId);

    // 更新檢舉的狀態（例如：未審核、審核通過、審核未通過）
    @Modifying
    @Transactional
    @Query("UPDATE GroupPurchaseReportVO g SET g.reportStatus = :reportStatus, g.updateAt = CURRENT_TIMESTAMP WHERE g.reportId = :reportId")
    int updateReportStatus(Integer reportId, Byte reportStatus);

    // 根據檢舉狀態統計檢舉的數量
    @Query("SELECT COUNT(g) FROM GroupPurchaseReportVO g WHERE g.reportStatus = :reportStatus")
    long countByReportStatus(Byte reportStatus);

}
