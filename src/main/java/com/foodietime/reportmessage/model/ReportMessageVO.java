package com.foodietime.reportmessage.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import com.foodietime.member.model.MemberVO;
import com.foodietime.message.model.MessageVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "REPORT_MESSAGE")
public class ReportMessageVO implements Serializable {

	@Id
	@Column(name = "REP_MES_ID")
	private Integer repMesId;

	@ManyToOne
	@JoinColumn(name = "MES_ID", referencedColumnName = "MES_ID")
	private MessageVO mesId;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "MEM_ID")
	private MemberVO memId;

	@Column(name = "REP_MES_DATE")
	private Timestamp repMesDate;

	@Column(name = "REP_MES_REASON")
	@NotNull(message = "檢舉原因請勿空白")
	@Min(value=1, message="最小1")
    @Max(value=255, message="最大255")
	private char repMesReason;

	@Column(name = "REP_MES_STATUS")
	private byte repMesStatus;

	public Integer getRepMesId() {
		return repMesId;
	}

	public void setRepMesId(Integer repMesId) {
		this.repMesId = repMesId;
	}

	public MessageVO getMesId() {
		return mesId;
	}

	public void setMesId(MessageVO mesId) {
		this.mesId = mesId;
	}

	public MemberVO getMemId() {
		return memId;
	}

	public void setMemId(MemberVO memId) {
		this.memId = memId;
	}

	public Timestamp getRepMesDate() {
		return repMesDate;
	}

	public void setRepMesDate(Timestamp repMesDate) {
		this.repMesDate = repMesDate;
	}

	public char getRepMesReason() {
		return repMesReason;
	}

	public void setRepMesReason(char repMesReason) {
		this.repMesReason = repMesReason;
	}

	public byte getRepMesStatus() {
		return repMesStatus;
	}

	public void setRepMesStatus(byte repMesStatus) {
		this.repMesStatus = repMesStatus;
	}

}
