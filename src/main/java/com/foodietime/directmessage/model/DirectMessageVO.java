package com.foodietime.directmessage.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class DirectMessageVO implements Serializable{
	private Integer dmId;
	private Integer memId;
	private Integer smgrId;
	private String messContent;
	private java.sql.Timestamp messTime;
	private Integer messDirection;
	public Integer getDmId() {
		return dmId;
	}
	public void setDmId(Integer dmId) {
		this.dmId = dmId;
	}
	public Integer getMemId() {
		return memId;
	}
	public void setMemId(Integer memId) {
		this.memId = memId;
	}
	public Integer getSmgrId() {
		return smgrId;
	}
	public void setSmgrId(Integer smgrId) {
		this.smgrId = smgrId;
	}
	public String getMessContent() {
		return messContent;
	}
	public void setMessContent(String messContent) {
		this.messContent = messContent;
	}
	public java.sql.Timestamp getMessTime() {
		return messTime;
	}
	public void setMessTime(java.sql.Timestamp messTime2) {
		this.messTime = messTime2;
	}
	public Integer getMessDirection() {
		return messDirection;
	}
	public void setMessDirection(Integer messDirection) {
		this.messDirection = messDirection;
	}
	
}
