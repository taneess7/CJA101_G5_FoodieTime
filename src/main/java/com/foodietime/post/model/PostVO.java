package com.foodietime.post.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import com.foodietime.favoritepost.model.FavoritePostVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.message.model.MessageVO;
import com.foodietime.postcategory.model.PostCategoryVO;
import com.foodietime.reportpost.model.ReportPostVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "POST")
public class PostVO implements Serializable {

	@Id
	@Column(name = "POST_ID")
	private Integer postId;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "MEM_ID")
	private MemberVO memId;

	@ManyToOne
	@JoinColumn(name = "POST_CATE_ID", referencedColumnName = "POST_CATE_ID")
	private PostCategoryVO postCateId;

	@Column(name = "POST_DATE")
	private Timestamp postDate;

	@Column(name = "POST_STATUS")
	private byte postStatus;

	@Column(name = "EDITDATE")
	private Timestamp editDate;

	@Column(name = "POST_TITLE")
	@NotNull(message = "標題請勿空白")
	@Min(value=1, message="最小1")
    @Max(value=100, message="最大100")
	private String postTitle;

	@Lob
	@Column(name = "POST_CONTENT", columnDefinition = "LONGTEXT")
	@NotNull(message = "內容請勿空白")
	private String postContent;

	@Column(name = "LIKE_COUNT")
	private Integer likeCount;

	@Column(name = "VIEWS")
	private Integer views;

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public MemberVO getMemId() {
		return memId;
	}

	public void setMemId(MemberVO memId) {
		this.memId = memId;
	}

	public PostCategoryVO getPostCateId() {
		return postCateId;
	}

	public void setPostCateId(PostCategoryVO postCateId) {
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

// ========== 對應多方 ==========
	@OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
	private Set<ReportPostVO> reportPost; // 這個分類底下的所有貼文

	@OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
	private Set<MessageVO> message;

	@OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
	private Set<FavoritePostVO> favoritePost;

	public Set<ReportPostVO> getReportPost() {
		return reportPost;
	}

	public void setReportPost(Set<ReportPostVO> reportPost) {
		this.reportPost = reportPost;
	}

	public Set<MessageVO> getMessage() {
		return message;
	}

	public void setMessage(Set<MessageVO> message) {
		this.message = message;
	}

	public Set<FavoritePostVO> getfavoritePost() {
		return favoritePost;
	}

	public void setFavoritePost(Set<FavoritePostVO> favoritePost) {
		this.favoritePost = favoritePost;
	}

}
