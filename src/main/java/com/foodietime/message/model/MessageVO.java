package com.foodietime.message.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class MessageVO implements Serializable{
	private Integer mesId;
	private Integer postId;
	private Integer memId;
	private Timestamp mesDate;
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
