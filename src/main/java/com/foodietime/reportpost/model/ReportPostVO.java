package com.foodietime.reportpost.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "REPORT_POST")
public class ReportPostVO implements Serializable {

	@Id
	@Column(name = "REP_POST_ID")
	private Integer repPostId;

	@ManyToOne
	@JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
	private PostVO postId;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "MEM_ID")
	private MemberVO memId;

	@Column(name = "REP_POST_DATE")
	private Timestamp repPostDate;

	@Column(name = "REP_POST_REASON")
	@NotNull(message = "檢舉原因請勿空白")
	@Min(value=1, message="最小1")
    @Max(value=255, message="最大255")
	private char repPostReason;

	@Column(name = "REP_POST_STATUS")
	private byte repPostStatus;

	public Integer getRepPostId() {
		return repPostId;
	}

	public void setRepPostId(Integer repPostId) {
		this.repPostId = repPostId;
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

	public Timestamp getRepPostDate() {
		return repPostDate;
	}

	public void setRepPostDate(Timestamp repPostDate) {
		this.repPostDate = repPostDate;
	}

	public char getRepPostReason() {
		return repPostReason;
	}

	public void setRepPostReason(char repPostReason) {
		this.repPostReason = repPostReason;
	}

	public byte getRepPostStatus() {
		return repPostStatus;
	}

	public void setRepPostStatus(byte repPostStatus) {
		this.repPostStatus = repPostStatus;
	}

}
