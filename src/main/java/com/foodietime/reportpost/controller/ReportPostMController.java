//package com.foodietime.reportpost.controller;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.foodietime.message.model.MessageService;
//import com.foodietime.post.model.PostService;
//import com.foodietime.reportpost.dto.ForumReportDTO;
//import com.foodietime.reportpost.model.ReportPostService;
//import com.foodietime.reportpost.model.ReportPostVO;
//
//import lombok.Getter;
//import lombok.Setter;
//
//@Controller
//@RequestMapping("/smg")
//public class ReportPostMController {
//
//	@Autowired
//	private ReportPostService reportPostService;
//
//	@Autowired
//	private MessageService messageService;
//	
//	@Autowired
//	private PostService postService;
//	
//	private static final Logger log = LoggerFactory.getLogger(ReportPostMController.class);
//
//	/**
//	 * 後台檢舉列表，可依狀態、貼文ID或關鍵字篩選
//	 */
//	@GetMapping("/admin-forum-reports")
//	public String listAllReports(@RequestParam(value = "status", required = false) Byte status,
//			@RequestParam(value = "postId", required = false) Integer postId,
//			@RequestParam(value = "keyword", required = false) String keyword,
//			@RequestParam(value = "reportType", required = false) String reportType,
//			@RequestParam(value = "page", defaultValue = "0") int page,
//			@RequestParam(value = "size", defaultValue = "10") int size,
//			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//			ModelMap model) {
//
//		Pageable pageable = PageRequest.of(page, size);
//		Page<ForumReportDTO> reportPage = reportPostService.findCombinedReportsForAdmin(status, postId, keyword,
//				reportType, startDate, endDate, pageable);
//
//		model.addAttribute("reportPage", reportPage);
//		model.addAttribute("currentStatus", status);
//		model.addAttribute("currentPostId", postId);
//		model.addAttribute("currentKeyword", keyword);
//		model.addAttribute("currentReportType", reportType);
//		model.addAttribute("currentStartDate", startDate);
//		model.addAttribute("currentEndDate", endDate);
//
//		return "admin/smg/admin-forum-reports"; // 需要建立對應的 Thymeleaf 視圖
//	}
//
//	/**
//	 * 顯示單一檢舉詳情
//	 */
//	@GetMapping("/detail")
//	public String viewReportDetail(@RequestParam("repPostId") Integer repPostId, ModelMap model,
//			RedirectAttributes redirectAttributes) {
//		ReportPostVO reportPostVO = reportPostService.getOneReportPost(repPostId);
//		if (reportPostVO == null) {
//			redirectAttributes.addFlashAttribute("errorMessage", "找不到指定的檢舉案件");
//			return "redirect:/smg/admin-forum-reports";
//		}
//		model.addAttribute("reportPostVO", reportPostVO);
//		return "admin/smg/admin-forum-reports/reportDetail"; // 需要建立對應的 Thymeleaf 視圖
//	}
//
//	/**
//	 * 更新單一檢舉的狀態
//	 */
//	@PostMapping("/updateStatus")
//	public String updateStatus(@RequestParam("repPostId") Integer repPostId, @RequestParam("status") Byte status,
//			RedirectAttributes redirectAttributes) {
//
//		ReportPostVO updatedReport = reportPostService.updateStatus(repPostId, status);
//
//		if (updatedReport != null) {
//			redirectAttributes.addFlashAttribute("success", "檢舉狀態更新成功");
//		} else {
//			redirectAttributes.addFlashAttribute("errorMessage", "更新失敗，找不到指定的檢舉案件");
//		}
//		/*************************** 2.開始新增資料 *****************************************/
//		// 更新後導回詳情頁，方便管理員查看結果
//		return "redirect:/admin/smg/admin-forum-reports/detail?repPostId=" + repPostId;
//	}
//
//	/**
//	 * 批次更新檢舉狀態
//	 */
//	@PostMapping("/batch")
//	public String batchAction(@RequestParam("action") String action,
//			@RequestParam("repPostIds") List<Integer> repPostIds,
//			@RequestParam(value = "status", required = false) Byte status, RedirectAttributes redirectAttributes) {
//		if (repPostIds == null || repPostIds.isEmpty()) {
//			redirectAttributes.addFlashAttribute("errorMessage", "請選擇要操作的檢舉案件");
//			return "redirect:/smg/admin-forum-reports";
//		}
//
//		int affectedRows = 0;
//		if ("updateStatus".equals(action)) {
//			if (status == null) {
//				redirectAttributes.addFlashAttribute("errorMessage", "請選擇要更新的狀態");
//				return "redirect:/admin/smg/admin-forum-reports";
//			}
//			// 假設 ReportPostService 中有 batchUpdateStatus 方法
//			affectedRows = reportPostService.batchUpdateStatus(repPostIds, status);
//			redirectAttributes.addFlashAttribute("success", "批次更新狀態成功，共影響 " + affectedRows + " 筆記錄");
//		} else {
//			redirectAttributes.addFlashAttribute("errorMessage", "無效的批量操作");
//		}
//
//		return "redirect:/admin/smg/admin-forum-reports";
//
//	}
//
//	// =================================================================
//	// API Endpoints for AJAX calls from the report list page
//	// =================================================================
//
//	/**
//	 * 專門用來接收前端 AJAX 請求的資料傳輸物件 (DTO)
//	 */
//	@Getter
//	@Setter
//	public static class ResolveReportRequest {
//		private Integer repPostId;
//		private String reportType;
//		private Integer contentId;
//	}
//	/**
//	 * 處理來自前端的「通過檢舉」請求 (AJAX)
//	 */
//	@PostMapping("/reports/resolve")
//	@ResponseBody
//	public ResponseEntity<?> resolveReport(@RequestBody ResolveReportRequest request) {
//		log.info("接收到檢舉處理請求: repPostId={}, reportType={}, contentId={}",
//				request.getRepPostId(), request.getReportType(), request.getContentId());
//		try {
//			// 將業務邏輯移至 Service 層，由 @Transactional 進行交易管理
//						reportPostService.resolveForumReport(request);
//
//			return ResponseEntity.ok().body(Map.of(
//					"success", true,
//					"action", "post".equals(request.getReportType()) ? "disabled_post" : "deleted_comment"
//			));
//		} catch (Exception e) {
//			log.error("處理檢舉 repPostId {} 時發生錯誤: {}", request.getRepPostId(), e.getMessage(), e);
//			return ResponseEntity.badRequest().body(Map.of(
//					"success", false,
//					"message", "操作失敗: " + e.getMessage()
//			));
//		}
//	}
//	
//	/**
//	 * 專門用來接收前端批量處理請求的 DTO
//	 */
//	@Getter
//	@Setter
//	public static class BatchResolveRequest {
//		private List<ResolveReportRequest> reports;
//	}
//	
//	/**
//	 * 處理來自前端的批量「通過檢舉」請求 (AJAX)
//	 */
//	@PostMapping("/reports/batch-resolve")
//	@ResponseBody
//	public ResponseEntity<?> batchResolveReports(@RequestBody BatchResolveRequest batchRequest) {
//		log.info("接收到批量處理檢舉請求: {} 筆", batchRequest.getReports().size());
//		try {
//			// 將業務邏輯移至 Service 層
//			int successCount = reportPostService.batchResolveForumReports(batchRequest.getReports());
//			return ResponseEntity.ok().body(Map.of(
//					"success", true,
//					"message", "成功處理 " + successCount + " 筆檢舉。"
//			));
//		} catch (Exception e) {
//			log.error("批量處理檢舉時發生錯誤: {}", e.getMessage(), e);
//			return ResponseEntity.badRequest().body(Map.of(
//					"success", false,
//					"message", "操作失敗: " + e.getMessage()
//			));
//		}
//	}
//	
//	/**
//	 * 專門用來接收前端批量駁回請求的 DTO
//	 */
//	@Getter
//	@Setter
//	public static class BatchActionRequest {
//	    private List<Integer> repPostIds;
//	}
//	/**
//	 * 處理來自前端的「駁回檢舉」請求 (AJAX)
//	 */
//	@PostMapping("/reports/reject")
//	@ResponseBody
//	public ResponseEntity<?> rejectReport(@RequestBody ResolveReportRequest request) {
//		log.info("接收到檢舉駁回請求: repPostId={}", request.getRepPostId());
//		try {
//			// Service 層只需更新狀態為 2 (已駁回)
//			reportPostService.updateStatus(request.getRepPostId(), (byte) 2);
//			return ResponseEntity.ok().body(Map.of("success", true, "message", "檢舉已駁回"));
//		} catch (Exception e) {
//			log.error("駁回檢舉 repPostId {} 時發生錯誤: {}", request.getRepPostId(), e.getMessage(), e);
//			return ResponseEntity.badRequest().body(Map.of(
//					"success", false,
//					"message", "操作失敗: " + e.getMessage()
//			));
//		}
//	}
//	
//	/**
//	 * 處理來自前端的批量「駁回檢舉」請求 (AJAX)
//	 */
//	@PostMapping("/reports/batch-reject")
//	@ResponseBody
//	public ResponseEntity<?> batchRejectReports(@RequestBody BatchActionRequest batchRequest) {
//		log.info("接收到批量駁回檢舉請求: {} 筆", batchRequest.getRepPostIds().size());
//		try {
//			// Service 層只需更新狀態為 2 (已駁回)
//			int successCount = reportPostService.batchUpdateStatus(batchRequest.getRepPostIds(), (byte) 2);
//			return ResponseEntity.ok().body(Map.of(
//					"success", true,
//					"message", "成功駁回 " + successCount + " 筆檢舉。"
//			));
//		} catch (Exception e) {
//			log.error("批量駁回檢舉時發生錯誤: {}", e.getMessage(), e);
//			return ResponseEntity.badRequest().body(Map.of(
//					"success", false,
//					"message", "操作失敗: " + e.getMessage()
//			));
//		}
//	}
//
//}
