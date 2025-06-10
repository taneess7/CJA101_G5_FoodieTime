package com.foodietime.reportpost.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.foodietime.member.model.MemberVO;

import jakarta.persistence.*;

@Entity
@Table(name = "REPORT_POST")
public class ReportPostVO implements Serializable{
	
	@Id
	@Column(name = "REP_POST_ID")
	private Integer repPostId;
	
	@ManyToOne
	@JoinColumn(name = "PostVO", referencedColumnName = "POST")
	private Integer postId;
	
	@ManyToOne
	@JoinColumn(name = "MemVO", referencedColumnName = "MEMBER")
	private MemberVO memId;
	
	@Column(name = "REP_POST_DATE")
	private Timestamp repPostDate;
	
	@Column(name = "REP_POST_REASON")
	private char repPostReason;
	
	@Column(name = "REP_POST_STATUS")
	private byte repPostStatus;
	
	public Integer getRepPostId() {
		return repPostId;
	}
	public void setRepPostId(Integer repPostId) {
		this.repPostId = repPostId;
	}
	public Integer getPostId() {
		return postId;
	}
	public void setPostId(Integer postId) {
		this.postId = postId;
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
