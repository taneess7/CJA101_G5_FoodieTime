package com.foodietime.favoritepost.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "FAVORITE POST")
@IdClass(FavoritePostPK.class)
public class FavoritePostVO implements Serializable{
	
	@Id	
	@Column(name = "postId", updatable = false)
	private Integer postId;

	@Column(name = "memId")
	private Integer memId;
	
	public FavoritePostVO(Integer postId, Integer memId) {
		super();
		this.postId = postId;
		this.memId = memId;
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
