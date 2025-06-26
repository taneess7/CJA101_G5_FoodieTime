package com.foodietime.directmessage.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DirectMessageDTO {

    private Integer dmId;
    private Integer memId;
    private String memName;
    private String messContent;
    private LocalDateTime messTime;
    private Integer messDirection;
    private String replyContent;
    private String formattedTime;
    private String replyAdminName; // 處理人員帳號
    private String replyStatus; // "已回覆" 或 "待處理"
    
    public String getSenderName() {
        if (messDirection == null) return "未知";
        if (messDirection == 0) return "會員";
        if (messDirection == 1) return "系統管理員";
        return "未知方向(" + messDirection + ")";
    }


}
