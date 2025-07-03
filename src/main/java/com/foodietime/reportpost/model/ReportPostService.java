package com.foodietime.reportpost.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodietime.directmessage.model.DirectMessageService;
import com.foodietime.directmessage.model.DirectMessageVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.message.model.MessageService;
import com.foodietime.post.model.PostRepository;
import com.foodietime.post.model.PostService;
import com.foodietime.post.model.PostVO;
import com.foodietime.reportmessage.model.ReportMessageRepository;
import com.foodietime.reportmessage.model.ReportMessageService;
import com.foodietime.reportmessage.model.ReportMessageVO;
import com.foodietime.reportpost.controller.ReportPostMController.ResolveReportRequest;
import com.foodietime.reportpost.dto.ForumReportDTO;
import com.foodietime.smg.model.SmgService;
import com.foodietime.smg.model.SmgVO;

@Service("ReportPostService")
public class ReportPostService {

	@Autowired
	private ReportPostRepository repository;

	@Autowired
	private ReportMessageService reportMessageService;

	@Autowired
	private PostService postService;

	@Autowired
	private MessageService messageService;
	
	@Autowired
	private ReportMessageRepository reportMessageRepository;
	
	@Autowired
	private DirectMessageService directMessageService;
	
	@Autowired
	private SmgService smgService;

	public ReportPostVO save(ReportPostVO vo) {
		return repository.save(vo);
	}

	public ReportPostVO updateReportPost(Integer repPostId, Integer postId, Integer memId,
			java.sql.Timestamp repPostDate, String repPostReason, byte repPostStatus) {
		ReportPostVO reportpostVO = new ReportPostVO();

		MemberVO member = new MemberVO();
		member.setMemId(memId);

		PostVO post = new PostVO();
		post.setPostId(postId);

		reportpostVO.setRepPostId(repPostId);
		reportpostVO.setPost(post);
		reportpostVO.setMember(member);
		reportpostVO.setRepPostDate(repPostDate);
		reportpostVO.setRepPostReason(repPostReason);
		reportpostVO.setRepPostStatus(repPostStatus);

		return repository.save(reportpostVO);
	}

	public void deleteReportPost(Integer repPostId) {
		repository.deleteById(repPostId);
	}

	public ReportPostVO getOneReportPost(Integer repPostId) {
		return repository.findById(repPostId).orElse(null);
	}

	public List<ReportPostVO> getAll() {
		return repository.findAll();
	}

	@Transactional
	public ReportPostVO updateStatus(Integer repPostId, byte newStatus) {
		ReportPostVO report = repository.findById(repPostId).orElse(null);
		if (report != null) {
			report.setRepPostStatus(newStatus);
			return repository.save(report);
		}
		return null;
	}

	/**
	 * 批次更新檢舉狀態
	 *
	 * @param repPostIds 要更新的檢舉ID列表
	 * @param status     新的狀態
	 * @return 影響的行數
	 */
	@Transactional
	public int batchUpdateStatus(List<Integer> repPostIds, byte newStatus) {
		if (repPostIds == null || repPostIds.isEmpty()) {
			return 0;
		}
		return repository.batchUpdateStatus(repPostIds, newStatus);
	}

	/**
	 * 整合貼文檢舉與留言檢舉，並轉換為 DTO 列表供後台使用。
	 */
	public Page<ForumReportDTO> findCombinedReportsForAdmin(Byte status, Integer postId, String keyword,
			String reportType, LocalDate startDate, LocalDate endDate, Pageable pageable) {
		// 1. 取得所有符合條件的貼文與留言檢舉
		List<ReportPostVO> postReports = new ArrayList<>();
		List<ReportMessageVO> messageReports = new ArrayList<>();

		if ("post".equals(reportType)) {
			postReports = repository.findForAdmin(status, postId, keyword, startDate, endDate);
		} else if ("comment".equals(reportType)) {
			messageReports = reportMessageService.findForAdmin(status, postId, keyword, startDate, endDate);
		} else { // null, "", or any other value (like "user" which we ignore for now)
			postReports = repository.findForAdmin(status, postId, keyword, startDate, endDate);
			messageReports = reportMessageService.findForAdmin(status, postId, keyword, startDate, endDate);
		}

		List<ForumReportDTO> combinedList = new ArrayList<>();

		// 2. 將兩種檢舉轉換為統一的 DTO 格式並合併
		postReports.forEach(pr -> {
			ForumReportDTO dto = new ForumReportDTO();
			dto.setRepPostId(pr.getRepPostId());
			dto.setPost(pr.getPost());
			dto.setMember(pr.getMember());
			dto.setRepPostDate(pr.getRepPostDate());
			dto.setRepPostReason(pr.getRepPostReason());
			dto.setRepPostStatus(pr.getRepPostStatus());
			combinedList.add(dto);
		});

		// 轉換留言檢舉
		messageReports.forEach(mr -> {
			ForumReportDTO dto = new ForumReportDTO();
			dto.setRepPostId(mr.getRepMesId());
			dto.setComment(mr.getMes());
			dto.setMember(mr.getMember());
			dto.setRepPostDate(mr.getRepMesDate());
			dto.setRepPostReason(mr.getRepMesReason());
			dto.setRepPostStatus(mr.getRepMesStatus());
			combinedList.add(dto);
		});

		// 3. 依檢舉時間降序排列 (處理 null 的情況)
		combinedList.sort(Comparator
				.comparing(ForumReportDTO::getRepPostDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

		// 4. 手動進行分頁
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<ForumReportDTO> pageList = (combinedList.size() < startItem) ? Collections.emptyList()
				: combinedList.subList(startItem, Math.min(startItem + pageSize, combinedList.size()));

		// 5. 建立並回傳 Page 物件
		return new PageImpl<>(pageList, pageable, combinedList.size());
	}

	@Transactional
	public void resolveForumReport(ResolveReportRequest request) {
		MemberVO targetMember = null; // 被檢舉內容的作者
		String contentTitle = ""; // 被檢舉內容的標題或摘要
		
		if ("post".equals(request.getReportType())) {
			// 取得被檢舉的貼文資訊
			PostVO post = postService.getOnePost(request.getContentId());
			if (post != null) {
				targetMember = post.getMember();
				contentTitle = post.getPostTitle();
			}
			// 貼文檢舉：下架貼文（狀態2）並標記檢舉為已回應（狀態1）
			postService.updateStatus(request.getContentId(), (byte) 2); // contentId 是 postId
			this.updateStatus(request.getRepPostId(), (byte) 1); // 'this' 指的是 ReportPostService 自身
		} else if ("comment".equals(request.getReportType())) {
			// 取得被檢舉的留言資訊
			com.foodietime.message.model.MessageVO message = messageService.getOneMessage(request.getContentId());
			if (message != null) {
				targetMember = message.getMember();
				contentTitle = message.getMesContent();
				if (contentTitle.length() > 30) {
					contentTitle = contentTitle.substring(0, 30) + "...";
				}
			}
			
			// 先檢查檢舉是否存在及其狀態
			ReportMessageVO reportMessage = reportMessageRepository.findById(request.getRepPostId())
				.orElseThrow(() -> new IllegalArgumentException("找不到指定的留言檢舉，ID: " + request.getRepPostId()));
			
			// 檢查檢舉是否已經被處理過
			if (reportMessage.getRepMesStatus() != 0) {
				throw new IllegalStateException("檢舉已經被處理過，無法重複處理。檢舉ID: " + request.getRepPostId());
			}
			
			// 檢查留言是否還存在
			if (messageService.getOneMessage(request.getContentId()) != null) {
				// 留言存在，直接刪除
				messageService.deleteMessage(request.getContentId()); // contentId 是 mesId
			}
			// 不論留言是否存在，都更新檢舉狀態為已處理
			reportMessage.setRepMesStatus((byte) 1);
			reportMessageRepository.save(reportMessage);
		} else {
			// 如果類型不對，拋出例外會讓 @Transactional 自動回復所有操作
			throw new IllegalArgumentException("無效的檢舉類型: " + request.getReportType());
		}
		
		// 發送通知給被檢舉內容的作者
		if (targetMember != null) {
			sendViolationNotification(targetMember, request.getReportType(), contentTitle);
		}
	}
	
	@Transactional
	public int batchResolveForumReports(List<ResolveReportRequest> reports) {
	    int successCount = 0;
	    for (ResolveReportRequest request : reports) {
	        try {
	            MemberVO targetMember = null;
	            String contentTitle = "";
	            
	            if ("post".equals(request.getReportType())) {
	                // 取得被檢舉的貼文資訊
	                PostVO post = postService.getOnePost(request.getContentId());
	                if (post != null) {
	                    targetMember = post.getMember();
	                    contentTitle = post.getPostTitle();
	                }
	                // 貼文檢舉：下架貼文（狀態2）並標記檢舉為已回應（狀態1）
	                postService.updateStatus(request.getContentId(), (byte) 2);
	                this.updateStatus(request.getRepPostId(), (byte) 1);
	            } else if ("comment".equals(request.getReportType())) {
	                // 取得被檢舉的留言資訊
	                com.foodietime.message.model.MessageVO message = messageService.getOneMessage(request.getContentId());
	                if (message != null) {
	                    targetMember = message.getMember();
	                    contentTitle = message.getMesContent();
	                    if (contentTitle.length() > 30) {
	                        contentTitle = contentTitle.substring(0, 30) + "...";
	                    }
	                }
	                
	                // 先檢查檢舉是否存在及其狀態
	                ReportMessageVO reportMessage = reportMessageRepository.findById(request.getRepPostId())
	                    .orElse(null);
	                
	                if (reportMessage == null) {
	                    // 檢舉不存在，跳過此項
	                    continue;
	                }
	                
	                // 檢查檢舉是否已經被處理過
	                if (reportMessage.getRepMesStatus() != 0) {
	                    // 已經處理過，跳過此項
	                    continue;
	                }
	                
	                // 檢查留言是否還存在
	                if (messageService.getOneMessage(request.getContentId()) != null) {
	                    // 留言存在，直接刪除
	                    messageService.deleteMessage(request.getContentId());
	                }
	                // 不論留言是否存在，都更新檢舉狀態為已處理
	                reportMessage.setRepMesStatus((byte) 1);
	                reportMessageRepository.save(reportMessage);
	            } else {
	                // 如果類型不對，跳過
	                continue;
	            }
	            
	            // 發送通知給被檢舉內容的作者
	            if (targetMember != null) {
	                sendViolationNotification(targetMember, request.getReportType(), contentTitle);
	            }
	            
	            successCount++;
	        } catch (Exception e) {
	            // 記錄錯誤但繼續處理其他檢舉
	            System.err.println("處理檢舉 ID " + request.getRepPostId() + " 時發生錯誤: " + e.getMessage());
	        }
	    }
	    return successCount;
	}
	
	/**
	 * 駁回單筆討論區檢舉（貼文或留言），將狀態更新為 2 (已駁回)。
	 * @param reportId 檢舉ID
	 * @param reportType 檢舉類型 ("post" 或 "comment")
	 */
	@Transactional
	public void rejectForumReport(Integer reportId, String reportType) {
	    final byte REJECTED_STATUS = 2; // 2 代表 "已駁回"

	    if ("post".equals(reportType)) {
	        // 這是貼文檢舉，更新 REPORT_POST 表
	        this.updateStatus(reportId, REJECTED_STATUS);
	    } else if ("comment".equals(reportType)) {
	        // 這是留言檢舉，更新 REPORT_MESSAGE 表
	        ReportMessageVO reportMessage = reportMessageRepository.findById(reportId)
	            .orElseThrow(() -> new IllegalArgumentException("找不到指定的留言檢舉，ID: " + reportId));
	        
	        // 檢查檢舉是否已經被處理過
	        if (reportMessage.getRepMesStatus() != 0) {
	            throw new IllegalStateException("檢舉已經被處理過，無法重複處理。檢舉ID: " + reportId);
	        }
	        
	        reportMessage.setRepMesStatus(REJECTED_STATUS);
	        reportMessageRepository.save(reportMessage);
	    } else {
	        throw new IllegalArgumentException("無效的檢舉類型: " + reportType);
	    }
	}

	/**
	 * 批量駁回多筆討論區檢舉，將狀態更新為 2 (已駁回)。
	 * @param reports 來自前端的請求資料列表
	 * @return 成功處理的筆數
	 */
	@Transactional
	public int batchRejectForumReports(List<ResolveReportRequest> reports) {
	    final byte REJECTED_STATUS = 2;

	    // 根據類型將 ID 分類
	    List<Integer> postReportIds = reports.stream()
	        .filter(r -> "post".equals(r.getReportType()))
	        .map(ResolveReportRequest::getRepPostId)
	        .collect(Collectors.toList());

	    List<Integer> commentReportIds = reports.stream()
	        .filter(r -> "comment".equals(r.getReportType()))
	        .map(ResolveReportRequest::getRepPostId)
	        .collect(Collectors.toList());

	    int affectedRows = 0;

	    // 批量更新貼文檢舉
	    if (!postReportIds.isEmpty()) {
	        affectedRows += this.batchUpdateStatus(postReportIds, REJECTED_STATUS);
	    }

	        // 批量更新留言檢舉
    if (!commentReportIds.isEmpty()) {
        List<ReportMessageVO> reportsToUpdate = reportMessageRepository.findAllById(commentReportIds);
        List<ReportMessageVO> validReports = new ArrayList<>();
        for (ReportMessageVO report : reportsToUpdate) {
            // 只更新未處理的檢舉
            if (report.getRepMesStatus() == 0) {
                report.setRepMesStatus(REJECTED_STATUS);
                validReports.add(report);
            }
        }
        if (!validReports.isEmpty()) {
            reportMessageRepository.saveAll(validReports);
            affectedRows += validReports.size();
        }
    }

	    return affectedRows;
	}

	/**
	 * 計算指定會員的貼文被檢舉次數（只計算已處理的檢舉，狀態為1）
	 * @param memberId 會員ID
	 * @return 被檢舉次數
	 */
	public long countReportsByMemberId(Integer memberId) {
		return repository.countByPost_Member_MemIdAndRepPostStatusIn(memberId, List.of((byte)1));
	}

	/**
	 * 檢查會員是否因檢舉過多而被禁止發文
	 * @param memberId 會員ID
	 * @param maxReports 最大允許檢舉次數，預設為5
	 * @return true 如果會員被禁止發文，false 否則
	 */
	public boolean isMemberBannedFromPosting(Integer memberId, int maxReports) {
		long reportCount = countReportsByMemberId(memberId);
		return reportCount >= maxReports;
	}

	/**
	 * 檢查會員是否因檢舉過多而被禁止發文（使用預設最大檢舉次數5）
	 * @param memberId 會員ID
	 * @return true 如果會員被禁止發文，false 否則
	 */
	public boolean isMemberBannedFromPosting(Integer memberId) {
		return isMemberBannedFromPosting(memberId, 5);
	}


	
	/**
	 * 發送違規通知給會員
	 */
	private void sendViolationNotification(MemberVO member, String reportType, String contentTitle) {
		try {
			
			
			// 取得系統管理員（假設 ID 為 1）
			SmgVO systemAdmin = smgService.findById(1);
			if (systemAdmin == null) {
				System.err.println("❌ 找不到系統管理員 (ID: 1)");
				return;
			}
			
			
			// 建立通知訊息
			DirectMessageVO notification = new DirectMessageVO();
			notification.setMember(member);
			notification.setSmgr(systemAdmin);
			notification.setMessDirection(DirectMessageVO.MessageDirection.ADMIN_TO_MEMBER);
			notification.setMessTime(LocalDateTime.now());
			
			// 根據檢舉類型設定不同的通知內容
			String notificationContent;
			if ("post".equals(reportType)) {
				notificationContent = String.format(
					"【系統通知】您的貼文「%s」因違反社群規範已被下架。如有疑問，請聯繫客服。",
					contentTitle
				);
			} else {
				notificationContent = String.format(
					"【系統通知】您的留言「%s」因違反社群規範已被刪除。如有疑問，請聯繫客服。",
					contentTitle
				);
			}
			
			notification.setMessContent(notificationContent);
			
			
			// 儲存通知
			DirectMessageVO savedNotification = directMessageService.addMessage(notification);
			
			
		} catch (Exception e) {
			// 記錄錯誤但不影響主要業務流程
			
			e.printStackTrace();
		}
	}
}
