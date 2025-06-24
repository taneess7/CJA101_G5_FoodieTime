package com.foodietime.groupbuyingcollectionlist.model;


import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductVO;
import com.foodietime.smgauth.model.SmgauthId;
import com.foodietime.smgauth.model.SmgauthVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="group_buying_collection_list")
@JsonIdentityInfo(
	    generator = ObjectIdGenerators.PropertyGenerator.class,
	    property  = "collectionId",
	    scope     = GroupBuyingCollectionListVO.class
	)
public class GroupBuyingCollectionListVO implements Serializable{
	
	@EmbeddedId
    @EqualsAndHashCode.Include
    private GroupBuyingCollectionListId id;

    @ManyToOne
    @MapsId("gbId")
    @JoinColumn(name = "GB_ID")
    private GroupBuyingCasesVO groupBuyingCase;
    
    @ManyToOne
    @MapsId("memId")
    @JoinColumn(name = "MEM_ID")
    private MemberVO member;

    @Column
    @NotNull(message = "建立時間: 請勿空白")  
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
    private LocalDateTime createAt;  // 建立時間

    
    public GroupBuyingCollectionListVO() {  //必需有一個不傳參數建構子
    	
    	
    }
  
    public Integer getMemId() {
        return id != null ? id.getMemId() : null;
    }

    public Integer getGbId() {
        return id != null ? id.getGbId() : null;
    }
 // 便利方法：設定會員ID和團購ID
    public void setMemIdAndGbId(Integer memId, Integer gbId) {
        if (this.id == null) {
            this.id = new GroupBuyingCollectionListId();
        }
        this.id.setMemId(memId);
        this.id.setGbId(gbId);
    }


	

	
}
