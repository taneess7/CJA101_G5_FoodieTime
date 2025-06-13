package com.foodietime.message.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;
import com.foodietime.reportmessage.model.ReportMessageVO;
import com.foodietime.reportpost.model.ReportPostVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "MESSAGE")
public class MessageVO implements Serializable {

	@Id
	@Column(name = "MES_ID")
	private Integer mesId;

	@ManyToOne
	@JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
	private PostVO postId;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "MEM_ID")
	private MemberVO memId;

	@Column(name = "MES_DATE")
	private Timestamp mesDate;

	@Column(name = "MES_CONTENT")
	@NotNull(message = "留言內容請勿空白")
	@Min(value=1, message="最小1")
    @Max(value=255, message="最大255")
	private String mesContent;

	public Integer getMesId() {
		return mesId;
	}

	public void setMesId(Integer mesId) {
		this.mesId = mesId;
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

	public Timestamp getMesDate() {
		return mesDate;
	}

	public void setMesDate(Timestamp mesDate) {
		this.mesDate = mesDate;
	}

	public String getMesContent() {
		return mesContent;
	}

	public void setMesContent(String mesContent) {
		this.mesContent = mesContent;
	}

	// ========== 對應多方 ==========
	@OneToMany(mappedBy = "mesId", cascade = CascadeType.ALL)
	private Set<ReportMessageVO> reportMessage; // 這個分類底下的所有留言

	public Set<ReportMessageVO> getReportMessage() {
		return reportMessage;
	}

	public void setReportMessage(Set<ReportMessageVO> reportMessage) {
		this.reportMessage = reportMessage;
	}

}
