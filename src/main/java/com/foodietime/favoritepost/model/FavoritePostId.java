package com.foodietime.favoritepost.model;

import java.io.Serializable;
import java.util.Objects;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Embeddable

public class FavoritePostId implements Serializable {

	
	@Column(name = "POST_ID")
	private Integer postId;

	@Column(name = "MEM_ID")
	private Integer memId;

	public FavoritePostId() {
	}
	
	public FavoritePostId(Integer postId, Integer memId) {
	this.postId = postId;
	this.memId = memId;
}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		FavoritePostId that = (FavoritePostId) obj;
		return Objects.equals(postId, that.postId) && Objects.equals(memId, that.memId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(postId, memId);
	}

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public Integer getMemId() {
		return memId;
	}

	public void setMemId(Integer memId) {
		this.memId = memId;
	}

}
