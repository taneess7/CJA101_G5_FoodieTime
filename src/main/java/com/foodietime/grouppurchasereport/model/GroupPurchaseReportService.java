package com.foodietime.grouppurchasereport.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupPurchaseReportService {

    @Autowired
    private GroupPurchaseReportRepository groupPurchaseReportRepository;

    // 查詢特定會員的所有檢舉紀錄
    public List<GroupPurchaseReportVO> getReportsByMemberId(Integer memId) {
        return groupPurchaseReportRepository.findByMember_MemId(memId);
    }

    // 查詢特定團購案的檢舉紀錄
    public List<GroupPurchaseReportVO> getReportsByGroupBuyingCaseId(Integer gbId) {
        return groupPurchaseReportRepository.findByGroupBuyingCase_GbId(gbId);
    }

    // 查詢檢舉狀態為指定狀態的檢舉紀錄
    public List<GroupPurchaseReportVO> getReportsByStatus(Byte reportStatus) {
        return groupPurchaseReportRepository.findByReportStatus(reportStatus);
    }

    // 查詢某會員對某一團購案的檢舉紀錄
    public List<GroupPurchaseReportVO> getReportByMemberAndGroupBuyingCase(Integer memId, Integer gbId) {
        return groupPurchaseReportRepository.findByMember_MemIdAndGroupBuyingCase_GbId(memId, gbId);
    }

    // 檢查某會員是否對某團購案件進行過檢舉
    public boolean checkIfReportExists(Integer memId, Integer gbId) {
        return groupPurchaseReportRepository.existsByMember_MemIdAndGroupBuyingCase_GbId(memId, gbId);
    }

    // 更新檢舉狀態
    public boolean updateReportStatus(Integer reportId, Byte reportStatus) {
        int rowsAffected = groupPurchaseReportRepository.updateReportStatus(reportId, reportStatus);
        return rowsAffected > 0;  // 如果有更新則返回 true，否則返回 false
    }

    // 統計某一檢舉狀態的檢舉數量
    public long countReportsByStatus(Byte reportStatus) {
        return groupPurchaseReportRepository.countByReportStatus(reportStatus);
    }

    // 新增檢舉紀錄（這裡假設你可能需要新增檢舉紀錄的功能）
    public GroupPurchaseReportVO addReport(GroupPurchaseReportVO report) {
        // 設置創建和更新時間
        report.setCreateAt(LocalDateTime.now());
        report.setUpdateAt(LocalDateTime.now());
        return groupPurchaseReportRepository.save(report);
    }
    // 查看全部檢舉
	public List<GroupPurchaseReportVO> getAll() {
		return groupPurchaseReportRepository.findAll();
	}

}
