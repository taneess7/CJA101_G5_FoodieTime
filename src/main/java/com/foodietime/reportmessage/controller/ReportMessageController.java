package com.foodietime.reportmessage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberVO;
import com.foodietime.message.model.MessageService;
import com.foodietime.message.model.MessageVO;
import com.foodietime.post.model.PostService;
import com.foodietime.post.model.PostVO;
import com.foodietime.reportmessage.model.ReportMessageService;
import com.foodietime.reportmessage.model.ReportMessageVO;
import com.foodietime.reportpost.model.ReportPostVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/reportmessage")
public class ReportMessageController {
	
	@Autowired
	private ReportMessageService reportMessageService;

	@Autowired
	MemService memservice;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private MessageService messageService;

	@GetMapping("/addReportMessage")
	public String addReportMessage(ModelMap model) {
		ReportMessageVO reportMessageVO = new ReportMessageVO();
		model.addAttribute("ReportMessageVO", reportMessageVO);
		return "back-end/reportpost/addReportMessage";
	}

	@PostMapping("/insert")
	public String insert(@Valid ReportMessageVO reportMessageVO, BindingResult result, ModelMap model, HttpSession session,
			@RequestParam("action") String action) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/	
		System.out.println("=== insert called ===");
		// ====== 測試用：手動指定登入會員 ======
		// 你可以改這個 ID 來測試不同會員
		MemberVO fakeMember = memservice.getById(2); // 2 改成你想測試的會員ID
		session.setAttribute("loginMember", fakeMember);
		// ====== 測試用結束 ======
		// 取得留言ID
	    Integer mesId = reportMessageVO.getMes().getMesId();
	    // 查詢完整留言
	    MessageVO fullMessage = messageService.getOneMessage(mesId);
	    // 取得貼文ID
	    Integer postId = fullMessage.getPost().getPostId();
	    // **把完整留言物件設回去，讓 JPA 關聯正確**
	    reportMessageVO.setMes(fullMessage);
		
	    if (result.hasErrors()) {
			 // 查詢該貼文與留言
	        PostVO postVO = postService.getOnePost(postId);
	        model.addAttribute("postVO", postVO);
	        List<MessageVO> messageList = messageService.getByPostId(postId);
	        model.addAttribute("messageList", messageList);
	        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
	        model.addAttribute("loginMember", loginMember);
	        model.addAttribute("messageVO", new MessageVO());
	        // 回到單一貼文頁
	        return "front/post/listOnePost";
		}
		// 設定留言檢舉時間
	    if (reportMessageVO.getRepMesDate() == null) {
	        reportMessageVO.setRepMesDate(new java.sql.Timestamp(System.currentTimeMillis()));
	    }
		/*************************** 2.開始新增資料 *****************************************/
		reportMessageService.save(reportMessageVO);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		List<ReportMessageVO> list = reportMessageService.getAll();
		model.addAttribute("reportMessageListData", list);

	    // 設定成功訊息到 session
	    session.setAttribute("reportSuccess", "檢舉成功，感謝您的協助！");
	    return "redirect:/post/one?postId=" + postId;
	}


}
