package com.foodietime.directmessage.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
@Entity
@Table(name = "direct_message")
public class DirectMessageVO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dm_id")
	private Integer dmId;
	@JoinColumn(name = "mem_id", nullable = false)
	private Integer memId;
	@JoinColumn(name = "smgr_id", nullable = true)
	private Integer smgrId;
	@Column(name = "mess_content", nullable = false)
	private String messContent;
	@Column(name = "mess_time", nullable = false)
	private java.sql.Timestamp messTime;
	@Column(name = "mess_direction", nullable = false)
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
