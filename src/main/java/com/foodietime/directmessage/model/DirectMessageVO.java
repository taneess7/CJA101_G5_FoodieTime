package com.foodietime.directmessage.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.foodietime.member.model.MemberVO;
import com.foodietime.smg.model.SmgVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "direct_message")
public class DirectMessageVO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dm_id")
	private Integer dmId;
	
	@ManyToOne
	@JoinColumn(name = "mem_id", nullable = false)
	private MemberVO member;
	
	@ManyToOne
	@JoinColumn(name = "smgr_id")
	private SmgVO smgrId;
	
	@Column(name = "mess_content", nullable = false)
	private String messContent;
	
	@Column(name = "mess_time", nullable = false)
	private LocalDateTime messTime;
	
	@Column(name = "mess_direction", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private MessageDirection messDirection;
	
	public Integer getDmId() {
		return dmId;
	}
	public void setDmId(Integer dmId) {
		this.dmId = dmId;
	}

	public MemberVO getMember() {
		return member;
	}
	public void setMember(MemberVO member) {
		this.member = member;
	}
	
	public SmgVO getSmgrId() {
		return smgrId;
	}
	public void setSmgrId(SmgVO smgrId) {
		this.smgrId = smgrId;
	}
	public String getMessContent() {
		return messContent;
	}
	public void setMessContent(String messContent) {
		this.messContent = messContent;
	}
	public LocalDateTime getMessTime() {
		return messTime;
	}
	public void setMessTime(LocalDateTime messTime) {
		this.messTime = messTime;
	}
	public MessageDirection getMessDirection() {
		return messDirection;
	}
	public void setMessDirection(MessageDirection messDirection) {
		this.messDirection = messDirection;
	}
	
	
	public enum MessageDirection {
	    MEMBER_TO_ADMIN, //0
	    ADMIN_TO_MEMBER  //1
	}
}
