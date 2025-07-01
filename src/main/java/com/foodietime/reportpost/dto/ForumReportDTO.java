package com.foodietime.reportpost.dto;

import com.foodietime.member.model.MemberVO;
import com.foodietime.message.model.MessageVO;
import com.foodietime.post.model.PostVO;
import lombok.Data;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 用於後台統一顯示貼文和留言檢舉的資料傳輸物件 (DTO)。
 */
@Data
public class ForumReportDTO {
	private Integer repPostId; // 注意：這裡會存放 ReportPost ID 或 ReportMessage ID
	private PostVO post; // 如果是留言檢舉，此欄位為 null
	private MessageVO comment; // 如果是貼文檢舉，此欄位為 null
	private MemberVO member;
	private Timestamp repPostDate;
	private String repPostReason;
	private byte repPostStatus;
	private String handlerName; // 處理人員姓名
    private Date handleDate;    // 處理時間

	// 檢舉類型判斷
	public boolean isPostReport() {
		return post != null;
	}

	public boolean isCommentReport() {
		return comment != null;
	}

	// 狀態檢查方法
	public boolean isPending() {
		return repPostStatus == 0;
	}

	public boolean isResolved() {
		return repPostStatus == 1;
	}
}