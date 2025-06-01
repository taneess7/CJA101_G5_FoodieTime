package com.foodietime.smg.model;

import java.io.Serializable;

public class SmgVO implements Serializable {
	private Integer smgId;
	private String smgrEmail;
	private String smgrAccount;
	private String smgrPassword;
	private String smgrName;
	private String smgrPhone;
	private Integer smgrStatus;
	
	public Integer getSmgrStatus() {
		return smgrStatus;
	}


	public void setSmgrStatus(Integer smgStatus) {
		this.smgrStatus = smgStatus;
	}


	public Integer getSmgId() {
		return smgId;
	}


	public void setSmgId(Integer smgId) {
		this.smgId = smgId;
	}


	public String getSmgrEmail() {
		return smgrEmail;
	}


	public void setSmgrEmail(String smgrEmail) {
		this.smgrEmail = smgrEmail;
	}


	public String getSmgrAccount() {
		return smgrAccount;
	}


	public void setSmgrAccount(String smgrAccount) {
		this.smgrAccount = smgrAccount;
	}


	public String getSmgrPassword() {
		return smgrPassword;
	}


	public void setSmgrPassword(String smgrPassword) {
		this.smgrPassword = smgrPassword;
	}


	public String getSmgrName() {
		return smgrName;
	}


	public void setSmgrName(String smgrName) {
		this.smgrName = smgrName;
	}


	public String getSmgrPhone() {
		return smgrPhone;
	}


	public void setSmgrPhone(String smgrPhone) {
		this.smgrPhone = smgrPhone;
	}


	public SmgVO() {
		super();
	}
	
	
}
