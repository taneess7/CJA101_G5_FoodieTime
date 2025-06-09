package com.foodietime.reportmessage.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

//import jakarta.persistence.*;

//@Entity
//@Table(name = "REPORT_MESSAGE")
public class ReportMessageVO implements Serializable{
	
//	@Id
//	@Column(name = "REP_MES_ID")
	private Integer repMesId;
	
//	@Column(name = "MES_ID")
	private Integer mesId;
	
//	@Column(name = "MEM_ID")
	private Integer memId;
	
//	@Column(name = "REP_MES_DATE")
	private Timestamp repMesDate;
	
//	@Column(name = "REP_MES_REASON")
	private char repMesReason;
	
//	@Column(name = "REP_MES_STATUS")
	private byte repMesStatus;
	
	public Integer getRepMesId() {
		return repMesId;
	}
	public void setRepMesId(Integer repMesId) {
		this.repMesId = repMesId;
	}
	public Integer getMesId() {
		return mesId;
	}
	public void setMesId(Integer mesId) {
		this.mesId = mesId;
	}
	public Integer getMemId() {
		return memId;
	}
	public void setMemId(Integer memId) {
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
