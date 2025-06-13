package com.foodietime.groupbuyingcollectionlist.model;

import java.io.Serializable;
import java.util.Objects;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.member.model.MemberVO;


public class GroupBuyingCollectionListId implements Serializable {

    private GroupBuyingCasesVO groupBuyingCase;  // 團購編號
    private MemberVO member;  // 會員編號

    // Constructors, equals, hashCode
    public GroupBuyingCollectionListId() {  // 無參數建構子

    }

    public GroupBuyingCollectionListId(GroupBuyingCasesVO groupBuyingCase, MemberVO member) {
        this.groupBuyingCase = groupBuyingCase;
        this.member = member;
    }


    //equals and hashCode 需要正確重寫
	@Override
	public int hashCode() {
		return Objects.hash(groupBuyingCase, member);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupBuyingCollectionListId other = (GroupBuyingCollectionListId) obj;
		return Objects.equals(groupBuyingCase, other.groupBuyingCase) && Objects.equals(member, other.member);
	}


}


