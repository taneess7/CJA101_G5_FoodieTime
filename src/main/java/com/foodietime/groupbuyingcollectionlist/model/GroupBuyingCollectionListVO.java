package com.foodietime.groupbuyingcollectionlist.model;


import java.io.Serializable;
import java.sql.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.member.model.MemberVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Data
@IdClass(GroupBuyingCollectionListId.class)  // 使用複合主鍵
@Table(name="group_buying_collection_list")
public class GroupBuyingCollectionListVO implements Serializable{
	
    @Id
    @ManyToOne
    @JoinColumn(name = "GB_ID", nullable = false)
    private GroupBuyingCasesVO groupBuyingCase;  // 團購編號

    @Id
    @ManyToOne
    @JoinColumn(name = "MEM_ID", nullable = false)
    private MemberVO member;  // 會員編號

    @Column
    @NotNull(message = "建立時間: 請勿空白")  
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;  // 建立時間
    
    public GroupBuyingCollectionListVO() {  //必需有一個不傳參數建構子
    	
    	
    }
}
