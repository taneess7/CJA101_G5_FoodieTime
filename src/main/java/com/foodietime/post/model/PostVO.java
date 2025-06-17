package com.foodietime.post.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import com.foodietime.favoritepost.model.FavoritePostVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.message.model.MessageVO;
import com.foodietime.postcategory.model.PostCategoryVO;
import com.foodietime.reportpost.model.ReportPostVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "POST")
public class PostVO implements Serializable {

	@Id
	@Column(name = "POST_ID")
	private Integer postId;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "MEM_ID")
	private MemberVO member;

	@ManyToOne
	@JoinColumn(name = "POST_CATE_ID", referencedColumnName = "POST_CATE_ID")
	private PostCategoryVO postCate;

	@Column(name = "POST_DATE")
	private Timestamp postDate;

	@Column(name = "POST_STATUS")
	private byte postStatus;

	@Column(name = "EDITDATE")
	private Timestamp editDate;

	@Column(name = "POST_TITLE")
	@NotNull(message = "標題請勿空白")
	@Size(min = 1, max = 100, message = "標題長度需介於1到100字")
	private String postTitle;

	@Lob
	@Column(name = "POST_CONTENT", columnDefinition = "LONGTEXT")
	@NotNull(message = "內容請勿空白")
	private String postContent;

	@Column(name = "LIKE_COUNT")
	private Integer likeCount;

	@Column(name = "VIEWS")
	private Integer views;

	

// ========== 對應多方 ==========
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private Set<ReportPostVO> reportPost; // 這個分類底下的所有貼文

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private Set<MessageVO> message;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private Set<FavoritePostVO> favoritePost;



}
