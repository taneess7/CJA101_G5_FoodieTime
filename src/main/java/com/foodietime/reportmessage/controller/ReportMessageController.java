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
import com.foodietime.favoritepost.model.FavoritePostService;

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

	@Autowired
private FavoritePostService favoritePostService;

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
    MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
    model.addAttribute("loginMember", loginMember);
    model.addAttribute("messageVO", new MessageVO());

    // 補齊 liked 和 bookmarked
    boolean liked = false; // 你有實作按讚功能再補正確判斷
    model.addAttribute("liked", liked);
    boolean bookmarked = false;
    if (loginMember != null) {
        // 這裡要注入 FavoritePostService
        bookmarked = favoritePostService.findByPK(postVO.getPostId(), loginMember.getMemId()) != null;
    }
    model.addAttribute("bookmarked", bookmarked);

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
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		List<ReportMessageVO> list = reportMessageService.getAll();
		model.addAttribute("reportMessageListData", list);

	    // 設定成功訊息到 session
	    session.setAttribute("reportSuccess", "檢舉成功，感謝您的協助！");
	    return "redirect:/post/one?postId=" + postId;
	}


}
