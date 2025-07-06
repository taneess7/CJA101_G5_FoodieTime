package com.foodietime.reportpost.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodietime.member.model.MemberVO;
import com.foodietime.message.model.MessageService;
import com.foodietime.post.model.PostService;
import com.foodietime.post.model.PostVO;
import com.foodietime.reportmessage.model.ReportMessageRepository;
import com.foodietime.reportmessage.model.ReportMessageService;
import com.foodietime.reportmessage.model.ReportMessageVO;
import com.foodietime.reportpost.controller.ReportPostMController.ResolveReportRequest;
import com.foodietime.reportpost.dto.ForumReportDTO;

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
	
	// 用於儲存處理人員和處理時間的靜態 Map
	private static final Map<Integer, ForumReportDTO> handlerInfoMap = new java.util.concurrent.ConcurrentHashMap<>();

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
			
			// 從 Map 中取得處理資訊
			ForumReportDTO handlerInfo = handlerInfoMap.get(pr.getRepPostId());
			if (handlerInfo != null) {
				dto.setHandlerName(handlerInfo.getHandlerName());
				dto.setHandleDate(handlerInfo.getHandleDate());
			}
			
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
			
			// 從 Map 中取得處理資訊
			ForumReportDTO handlerInfo = handlerInfoMap.get(mr.getRepMesId());
			if (handlerInfo != null) {
				dto.setHandlerName(handlerInfo.getHandlerName());
				dto.setHandleDate(handlerInfo.getHandleDate());
			}
			
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
			// 先檢查檢舉是否存在及其狀態
			ReportMessageVO reportMessage = reportMessageRepository.findById(request.getRepPostId())
				.orElseThrow(() -> new IllegalArgumentException("找不到指定的留言檢舉，ID: " + request.getRepPostId()));
			
			// 檢查檢舉是否已經被處理過
			if (reportMessage.getRepMesStatus() != 0) {
				throw new IllegalStateException("檢舉已經被處理過，無法重複處理。檢舉ID: " + request.getRepPostId());
			}
			
			// 取得被檢舉的留言資訊（只呼叫一次）
			com.foodietime.message.model.MessageVO message = messageService.getOneMessage(request.getContentId());
			if (message != null) {
				targetMember = message.getMember();
				contentTitle = message.getMesContent();
				if (contentTitle.length() > 30) {
					contentTitle = contentTitle.substring(0, 30) + "...";
				}
				
				// 先將檢舉記錄的 MES_ID 設為 NULL，避免外鍵約束問題
				updateReportMessageAndDeleteComment(reportMessage, request.getContentId());
			} else {
				// 留言不存在，只更新檢舉狀態
				reportMessage.setRepMesStatus((byte) 1);
				reportMessageRepository.save(reportMessage);
			}
		} else {
			// 如果類型不對，拋出例外會讓 @Transactional 自動回復所有操作
			throw new IllegalArgumentException("無效的檢舉類型: " + request.getReportType());
		}
		
		// 記錄處理人員和處理時間到 DTO 中
		if (request.getHandlerName() != null) {
			updateReportDTOWithHandlerInfo(request.getRepPostId(), request.getHandlerName(), request.getReportType());
		}
		
		// 發送通知給被檢舉內容的作者
		if (targetMember != null) {
			// 檢舉通知現在統一在收件夾顯示，不需要額外發送 DirectMessage
			// sendViolationNotification(targetMember, request.getReportType(), contentTitle);
		}
	}
	
	/**
	 * 更新檢舉記錄並軟刪除留言的輔助方法
	 */
	@Transactional
	private void updateReportMessageAndDeleteComment(ReportMessageVO reportMessage, Integer mesId) {
		// 先軟刪除留言（將狀態設為已刪除，但不真正刪除記錄）
		try {
			messageService.deleteMessage(mesId);
		} catch (Exception e) {
			// 如果刪除失敗，記錄錯誤但繼續處理檢舉狀態
			System.err.println("刪除留言 ID " + mesId + " 時發生錯誤: " + e.getMessage());
		}
		
		// 更新檢舉記錄狀態為已處理
		reportMessage.setRepMesStatus((byte) 1);
		reportMessageRepository.save(reportMessage);
	}
	
	@Transactional
	private void deleteComment(Integer mesId) {
		try {
			messageService.deleteMessage(mesId);
		} catch (Exception e) {
			// 如果刪除失敗，記錄錯誤但繼續處理檢舉狀態
			System.err.println("刪除留言 ID " + mesId + " 時發生錯誤: " + e.getMessage());
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
	                
	                // 取得被檢舉的留言資訊（只呼叫一次）
	                com.foodietime.message.model.MessageVO message = messageService.getOneMessage(request.getContentId());
	                if (message != null) {
	                    targetMember = message.getMember();
	                    contentTitle = message.getMesContent();
	                    if (contentTitle.length() > 30) {
	                        contentTitle = contentTitle.substring(0, 30) + "...";
	                    }
	                    
	                    // 先將檢舉記錄的 MES_ID 設為 NULL，避免外鍵約束問題
	                    updateReportMessageAndDeleteComment(reportMessage, request.getContentId());
	                } else {
	                    // 留言不存在，只更新檢舉狀態
	                    reportMessage.setRepMesStatus((byte) 1);
	                    reportMessageRepository.save(reportMessage);
	                }
	            } else {
	                // 如果類型不對，跳過
	                continue;
	            }
	            
	            // 發送通知給被檢舉內容的作者
	            if (targetMember != null) {
	                // 檢舉通知現在統一在收件夾顯示，不需要額外發送 DirectMessage
	                // sendViolationNotification(targetMember, request.getReportType(), contentTitle);
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
	 * @param handlerName 處理人員姓名（可選）
	 */
	@Transactional
	public void rejectForumReport(Integer reportId, String reportType, String handlerName) {
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
	    
	    // 記錄處理人員和處理時間到 DTO 中
	    if (handlerName != null) {
	        updateReportDTOWithHandlerInfo(reportId, handlerName, reportType);
	    }
	}
	
	/**
	 * 駁回單筆討論區檢舉（貼文或留言），將狀態更新為 2 (已駁回)。
	 * @param reportId 檢舉ID
	 * @param reportType 檢舉類型 ("post" 或 "comment")
	 */
	@Transactional
	public void rejectForumReport(Integer reportId, String reportType) {
	    rejectForumReport(reportId, reportType, null);
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
		return isMemberBannedFromPosting(memberId, 100);
	}

	/**
	 * 取得指定會員的所有檢舉通知（使用 ForumReportDTO）
	 * @param memberId 會員ID
	 * @return 檢舉通知列表
	 */
	public List<ForumReportDTO> getNotificationsByMemberId(Integer memberId) {
	    List<ForumReportDTO> notifications = new ArrayList<>();

	    // 查詢這個會員發的貼文被檢舉
	    List<ReportPostVO> postReports = repository.findByPost_Member_MemId(memberId);
	    for (ReportPostVO report : postReports) {
	        ForumReportDTO dto = new ForumReportDTO();
	        dto.setRepPostId(report.getRepPostId());
	        dto.setPost(report.getPost());
	        // 這裡設定被檢舉人（貼文作者）
	        dto.setMember(report.getPost().getMember());
	        dto.setRepPostDate(report.getRepPostDate());
	        dto.setRepPostReason(report.getRepPostReason());
	        dto.setRepPostStatus(report.getRepPostStatus());
	        notifications.add(dto);
	    }

	    // 查詢這個會員發的留言被檢舉
	    List<ReportMessageVO> messageReports = reportMessageRepository.findByMes_Member_MemId(memberId);
	    for (ReportMessageVO report : messageReports) {
	        ForumReportDTO dto = new ForumReportDTO();
	        dto.setRepPostId(report.getRepMesId());
	        dto.setComment(report.getMes());
	        // 這裡設定被檢舉人（留言作者）
	        dto.setMember(report.getMes().getMember());
	        dto.setRepPostDate(report.getRepMesDate());
	        dto.setRepPostReason(report.getRepMesReason());
	        dto.setRepPostStatus(report.getRepMesStatus());
	        notifications.add(dto);
	    }

	    notifications.sort((a, b) -> b.getRepPostDate().compareTo(a.getRepPostDate()));
	    return notifications;
	}

	/**
	 * 取得指定檢舉的 ForumReportDTO（用於後台詳情頁面）
	 * @param repPostId 檢舉ID
	 * @return ForumReportDTO 或 null（如果找不到）
	 */
	public ForumReportDTO getOneForumReportDTO(Integer repPostId) {
		ForumReportDTO dto = null;
		
		// 先嘗試從貼文檢舉中查找
		ReportPostVO reportPost = repository.findById(repPostId).orElse(null);
		if (reportPost != null) {
			dto = new ForumReportDTO();
			dto.setRepPostId(reportPost.getRepPostId());
			dto.setPost(reportPost.getPost());
			dto.setMember(reportPost.getMember());
			dto.setRepPostDate(reportPost.getRepPostDate());
			dto.setRepPostReason(reportPost.getRepPostReason());
			dto.setRepPostStatus(reportPost.getRepPostStatus());
		} else {
			// 如果找不到貼文檢舉，嘗試從留言檢舉中查找
			ReportMessageVO reportMessage = reportMessageRepository.findById(repPostId).orElse(null);
			if (reportMessage != null) {
				dto = new ForumReportDTO();
				dto.setRepPostId(reportMessage.getRepMesId());
				dto.setComment(reportMessage.getMes());
				dto.setMember(reportMessage.getMember());
				dto.setRepPostDate(reportMessage.getRepMesDate());
				dto.setRepPostReason(reportMessage.getRepMesReason());
				dto.setRepPostStatus(reportMessage.getRepMesStatus());
			}
		}
		
		// 如果有找到檢舉資料，嘗試從 Map 中取得處理資訊
		if (dto != null) {
			ForumReportDTO handlerInfo = handlerInfoMap.get(repPostId);
			if (handlerInfo != null) {
				dto.setHandlerName(handlerInfo.getHandlerName());
				dto.setHandleDate(handlerInfo.getHandleDate());
			}
		}
		
		return dto;
	}

	/**
	 * 更新 ForumReportDTO 中的處理人員和處理時間資訊
	 * @param repPostId 檢舉ID
	 * @param handlerName 處理人員姓名
	 * @param reportType 檢舉類型
	 */
	private void updateReportDTOWithHandlerInfo(Integer repPostId, String handlerName, String reportType) {
		// 建立一個包含處理資訊的 DTO
		ForumReportDTO dto = new ForumReportDTO();
		dto.setRepPostId(repPostId);
		dto.setHandlerName(handlerName);
		dto.setHandleDate(new java.util.Date());
		
		// 將處理資訊儲存到靜態 Map 中
		handlerInfoMap.put(repPostId, dto);
	}
}
