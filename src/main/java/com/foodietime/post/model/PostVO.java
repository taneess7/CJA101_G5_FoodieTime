package com.foodietime.post.model;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.*;

//@Entity
//@Table(name = "POST")
public class PostVO implements Serializable {
	
//	@Id
//	@Column(name = "POST_ID")
	private Integer postId;
	
//	@ManyToOne
//	@JoinColumn(name = "MemVO", referencedColumnName = "MEMBER")
//	@Column(name = "MEM_ID")
	private Integer memId;
	
//	@ManyToOne
//	@JoinColumn(name = "PostCategoryVO", referencedColumnName = "POST_CATEGORY")
//	@Column(name = "POST_CATE_ID")
	private Integer postCateId;
	
//	@Column(name = "POST_DATE")
	private Timestamp postDate;
	
//	@Column(name = "POST_STATUS")
	private byte postStatus;
	
//	@Column(name = "EDITDATE")
	private Timestamp editDate;
	
//	@Column(name = "POST_TITLE")
	private String postTitle;
	
//	@Column(name = "POST_CONTENT")
	private String postContent;
	
//	@Column(name = "LIKE_COUNT")
	private Integer likeCount;
	
//	@Column(name = "VIEWS")
	private Integer views;

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

	public Integer getPostCateId() {
		return postCateId;
	}

	public void setPostCateId(Integer postCateId) {
		this.postCateId = postCateId;
	}

	public Timestamp getPostDate() {
		return postDate;
	}

	public void setPostDate(Timestamp postDate) {
		this.postDate = postDate;
	}

	public byte getPostStatus() {
		return postStatus;
	}

	public void setPostStatus(byte postStatus) {
		this.postStatus = postStatus;
	}

	public Timestamp getEditdate() {
		return editDate;
	}

	public void setEditDate(Timestamp editDate) {
		this.editDate = editDate;
	}

	public String getPostTitle() {
		return postTitle;
	}

	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}

	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}

	public Integer getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

}
