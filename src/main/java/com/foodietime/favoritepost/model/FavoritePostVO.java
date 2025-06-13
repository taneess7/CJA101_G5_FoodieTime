package com.foodietime.favoritepost.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;

import jakarta.persistence.*;

@Entity
@Table(name = "FAVORITE_POST")
public class FavoritePostVO implements Serializable {

	@EmbeddedId
	private FavoritePostId Id;

	@Column(name = "POST_ID")
	private Integer postId;

	@Column(name = "MEM_ID")
	private Integer memId;

//	public FavoritePostVO(PostVO postId, MemberVO memId) {
//		super();
//		this.postId = postId;
//		this.memId = memId;
//	}

	public FavoritePostVO() {
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
