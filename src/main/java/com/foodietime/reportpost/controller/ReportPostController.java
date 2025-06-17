package com.foodietime.reportpost.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodietime.post.model.PostVO;
import com.foodietime.reportpost.model.ReportPostService;
import com.foodietime.reportpost.model.ReportPostVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/reportpost")
public class ReportPostController {

	@Autowired
	private ReportPostService reportPostService;

	@GetMapping("/addReportPost")
	public String addReportPost(ModelMap model) {
		ReportPostVO reportPostVO = new ReportPostVO();
		model.addAttribute("ReportPostVO", reportPostVO);
		return "back-end/reportpost/addReportPost";
	}

	@PostMapping("/insert")
	public String insert(@Valid ReportPostVO reportPostVO, BindingResult result, ModelMap model,
			@RequestParam("action") String action) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		if (result.hasErrors()) {
			return "back-end/reportpost/addReportPost";
		}
		/*************************** 2.開始新增資料 *****************************************/
		reportPostService.addReportPost(reportPostVO.getMember().getMemId(), reportPostVO.getPost().getPostId(),
				reportPostVO.getRepPostDate(), reportPostVO.getRepPostReason(), reportPostVO.getRepPostStatus());
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<ReportPostVO> list = reportPostService.getAll();
		model.addAttribute("reportPostListData", list);
		model.addAttribute("success", "新增成功");
		return "redirect:/reportpost/listAllReportPost";
	}

	@PostMapping("/update")
	public String update(@Valid ReportPostVO reportPostVO, BindingResult result, ModelMap model) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		if (result.hasErrors()) {
			return "back-end/reportpost/updateReportPost";
		}
		/*************************** 2.開始新增資料 *****************************************/
		// 假設你從 DB 撈出原本的資料來比較
		reportPostService.updateReportPost(reportPostVO.getRepPostId(),reportPostVO.getMember().getMemId(), reportPostVO.getPost().getPostId(),
				reportPostVO.getRepPostDate(), reportPostVO.getRepPostReason(), reportPostVO.getRepPostStatus());
		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "修改成功");
		model.addAttribute("reportPostVO", reportPostVO);
		return "redirect:/reportpost/listAllReportPost";
	}
	
	@PostMapping("/delete")
	public String delete(@RequestParam("reportpostId") String repPostId, ModelMap model) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/

		/*************************** 2.開始刪除資料 *****************************************/
		reportPostService.deleteReportPost(Integer.valueOf(repPostId));
		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
		List<ReportPostVO> list = reportPostService.getAll();
		model.addAttribute("reportPostListData", list);
		model.addAttribute("success", "刪除成功");
		return "redirect:/reportpost/listAllReportPost";
	}
	
	// ================ GET ONE 查詢單一貼文 ================
	@PostMapping("/getOne_For_Display")
	public String getOne_For_Display(@RequestParam("reportpostId") String reportpostId, ModelMap model) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		Integer id = null;
		try {
			id = Integer.valueOf(reportpostId);
		} catch (NumberFormatException e) {
			model.addAttribute("errorMessage", "貼文編號格式不正確");
			return "back-end/reportpost/select_page";
		}

		/*************************** 2.開始查詢資料 *****************************************/
		ReportPostVO reportPostVO = reportPostService.getOneReportPost(id);
		if (reportPostVO == null) {
			model.addAttribute("errorMessage", "查無資料");
			return "back-end/reportpost/select_page";
		}

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("reportpostVO", reportPostVO);
		return "back-end/reportpost/listOneReportPost";
	}

	// ================ GET ALL 查詢所有貼文 ================
	@GetMapping("/listAllReportPost")
	public String listAllPost(ModelMap model) {

		/**************************** 1.接收請求參數 (無參數)************************************/

		/*************************** 2.開始查詢資料 *****************************************/
		List<ReportPostVO> list = reportPostService.getAll();

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("reportPostListData", list);
		return "back-end/reportpost/listAllPost";
	}

	// ================ 選擇頁面 ================
	@GetMapping("/select_page")
	public String select_page() {
		return "back-end/reportpost/select_page";
	}

}
