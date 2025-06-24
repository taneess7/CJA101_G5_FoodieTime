package com.foodietime.grouppurchasereport.model;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.member.model.MemberVO;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Data
@Table(name = "group_purchase_report")
@JsonIdentityInfo(
	    generator = ObjectIdGenerators.PropertyGenerator.class,
	    property  = "reportId",
	    scope     = GroupPurchaseReportVO.class
	)
public class GroupPurchaseReportVO implements Serializable {

    @Id
    @Column(name = "REPORT_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private Integer reportId;  // 檢舉紀錄編號

    
    @ManyToOne
    @JoinColumn(name = "MEM_ID", nullable = false)  
    private MemberVO member;  // 會員編號 
    

    @ManyToOne
    @JoinColumn(name = "GB_ID", nullable = false)  
    private GroupBuyingCasesVO groupBuyingCase;  // 團購案 
    

    @Column(name = "REPORT_REASON", nullable = false, length = 255)
    @NotNull(message = "檢舉理由: 請勿空白")
    private String reportReason;  // 檢舉理由 (簡要)

    
    @Column(name = "REPORT_DETAIL", nullable = true, length = 1000)
    private String reportDetail;  // 檢舉詳情 (補充)

    
    @Column(name = "REPORT_STATUS", nullable = false)
    @NotNull(message = "檢舉狀態: 請勿空白")
    private Byte reportStatus;  // 檢舉狀態 (0: 未審核 1: 審核通過 2: 審核未通過)

    @Column(name = "CREATE_AT", nullable = false)
    @NotNull(message = "建立時間: 請勿空白")   
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  
    private LocalDateTime createAt;  // 建立時間


    @Column(name = "UPDATE_AT", nullable = false)
    @NotNull(message = "更新時間: 請勿空白")   
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
    private LocalDateTime updateAt;  // 更新時間


    public GroupPurchaseReportVO() {
        // 無參數建構子
    }
}
