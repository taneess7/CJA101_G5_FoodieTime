package com.foodietime.favoritepost.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;

import jakarta.persistence.*;

@Entity
@Table(name = "FAVORITE_POST")
@IdClass(FavoritePostPK.class)
public class FavoritePostVO implements Serializable{
	
	@Id	
	@ManyToOne
	@JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
	private PostVO postId;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "MEM_ID")
	private MemberVO memId;
	
	public FavoritePostVO(PostVO postId, MemberVO memId) {
		super();
		this.postId = postId;
		this.memId = memId;
	}

	public PostVO getPostId() {
		return postId;
	}

	public void setPostId(PostVO postId) {
		this.postId = postId;
	}

	public MemberVO getMemId() {
		return memId;
	}

	public void setMemId(MemberVO memId) {
		this.memId = memId;
	}

	

}
