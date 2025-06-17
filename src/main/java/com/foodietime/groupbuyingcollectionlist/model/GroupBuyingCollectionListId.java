package com.foodietime.groupbuyingcollectionlist.model;

import java.io.Serializable;
import java.util.Objects;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.member.model.MemberVO;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;


@Embeddable
public class GroupBuyingCollectionListId implements Serializable {

	@Column(name = "MEM_ID")
    private Integer memId;  // 會員編號
	@Column(name = "GB_ID")
    private Integer gbId;   // 團購編號

    public GroupBuyingCollectionListId() {  // 無參數建構子

    }

    public GroupBuyingCollectionListId(Integer memId, Integer gbId) {
        this.memId = memId;
        this.gbId = gbId;
    }
    

    
    public Integer getMemId() {
		return memId;
	}

	public void setMemId(Integer memId) {
		this.memId = memId;
	}

	public Integer getGbId() {
		return gbId;
	}

	public void setGbId(Integer gbId) {
		this.gbId = gbId;
	}

	//equals and hashCode 需要正確重寫
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupBuyingCollectionListId other = (GroupBuyingCollectionListId) obj;
		return Objects.equals(gbId, other.gbId) && Objects.equals(memId, other.memId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(gbId, memId);
	}
    
    
    
	
	

	


}


