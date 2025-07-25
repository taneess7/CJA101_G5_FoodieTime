package com.foodietime.member.model;

import lombok.Data;

@Data
public class MemberDTO {
    private Integer memId;
    private String memName;
    private String memEmail;
    private String avatarBase64;
    private String memPhone;
    private Integer memStatus;
    private Integer memNoSpeak;
    private Integer memNoPost;
    private Integer memNoGroup;
    private Integer memNoJoinGroup;
    private String memTime; // 註冊日期 yyyy-MM-dd
    private String lastModifiedDate; // yyyy-MM-dd
    private String lastModifiedBy; // ex. "系統管理員"
    
    private Integer memGender;
    private Integer orderCount;
    private Integer totalSpent;
}
