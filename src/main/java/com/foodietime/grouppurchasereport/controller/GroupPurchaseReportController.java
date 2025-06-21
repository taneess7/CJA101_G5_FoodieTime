package com.foodietime.grouppurchasereport.controller;

import com.foodietime.grouppurchasereport.model.GroupPurchaseReportService;
import com.foodietime.grouppurchasereport.model.GroupPurchaseReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group-purchase-reports")
public class GroupPurchaseReportController {

    @Autowired
    private GroupPurchaseReportService groupPurchaseReportService;

    // 查詢特定會員的所有檢舉紀錄
    @GetMapping("/member/{memId}")
    public ResponseEntity<List<GroupPurchaseReportVO>> getReportsByMemberId(@PathVariable Integer memId) {
        List<GroupPurchaseReportVO> reports = groupPurchaseReportService.getReportsByMemberId(memId);
        return ResponseEntity.ok(reports);
    }

    // 查詢特定團購案的檢舉紀錄
    @GetMapping("/group-buying-case/{gbId}")
    public ResponseEntity<List<GroupPurchaseReportVO>> getReportsByGroupBuyingCaseId(@PathVariable Integer gbId) {
        List<GroupPurchaseReportVO> reports = groupPurchaseReportService.getReportsByGroupBuyingCaseId(gbId);
        return ResponseEntity.ok(reports);
    }

    // 查詢檢舉狀態為指定狀態的檢舉紀錄
    @GetMapping("/status/{reportStatus}")
    public ResponseEntity<List<GroupPurchaseReportVO>> getReportsByStatus(@PathVariable Byte reportStatus) {
        List<GroupPurchaseReportVO> reports = groupPurchaseReportService.getReportsByStatus(reportStatus);
        return ResponseEntity.ok(reports);
    }

    // 查詢某會員對某一團購案的檢舉紀錄
    @GetMapping("/member/{memId}/group-buying-case/{gbId}")
    public ResponseEntity<List<GroupPurchaseReportVO>> getReportByMemberAndGroupBuyingCase(
            @PathVariable Integer memId, @PathVariable Integer gbId) {
        List<GroupPurchaseReportVO> reports = groupPurchaseReportService.getReportByMemberAndGroupBuyingCase(memId, gbId);
        return ResponseEntity.ok(reports);
    }

    // 檢查某會員是否對某團購案件進行過檢舉
    @GetMapping("/check/{memId}/{gbId}")
    public ResponseEntity<Boolean> checkIfReportExists(@PathVariable Integer memId, @PathVariable Integer gbId) {
        boolean exists = groupPurchaseReportService.checkIfReportExists(memId, gbId);
        return ResponseEntity.ok(exists);
    }

    // 更新檢舉狀態
    @PutMapping("/update/{reportId}")
    public ResponseEntity<String> updateReportStatus(
            @PathVariable Integer reportId, @RequestBody Byte reportStatus) {
        boolean isUpdated = groupPurchaseReportService.updateReportStatus(reportId, reportStatus);
        if (isUpdated) {
            return ResponseEntity.ok("檢舉狀態已更新");
        } else {
            return ResponseEntity.status(400).body("檢舉狀態更新失敗");
        }
    }

    // 統計某一檢舉狀態的檢舉數量
    @GetMapping("/count/status/{reportStatus}")
    public ResponseEntity<Long> countReportsByStatus(@PathVariable Byte reportStatus) {
        long count = groupPurchaseReportService.countReportsByStatus(reportStatus);
        return ResponseEntity.ok(count);
    }

    // 新增檢舉紀錄
    @PostMapping("/add")
    public ResponseEntity<GroupPurchaseReportVO> addReport(@RequestBody GroupPurchaseReportVO report) {
        GroupPurchaseReportVO savedReport = groupPurchaseReportService.addReport(report);
        return ResponseEntity.status(201).body(savedReport);
    }
}
