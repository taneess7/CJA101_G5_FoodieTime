package com.foodietime.message.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "POST")
public class MessageVO implements Serializable{
	
	@Id
	@Column(name = "MES_ID")
	private Integer mesId;
	
	@ManyToOne
	@JoinColumn(name = "PostVO", referencedColumnName = "MEMBER")
	private Integer postId;
	
	@ManyToOne
	@JoinColumn(name = "MemberVO", referencedColumnName = "MEMBER")
	private Integer memId;
	
	@Column(name = "MES_DATE")
	private Timestamp mesDate;
	
	@Column(name = "MES_CONTENT")
	private String mesContent;
	
	public Integer getMesId() {
		return mesId;
	}
	public void setMesId(Integer mesId) {
		this.mesId = mesId;
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
	
	
}
