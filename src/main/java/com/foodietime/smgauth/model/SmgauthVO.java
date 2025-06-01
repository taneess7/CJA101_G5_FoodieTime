package com.foodietime.smgauth.model;

import java.io.Serializable;

public class SmgauthVO implements Serializable{
	private Integer smgFuncId;
	private Integer smgId;
	public Integer getSmgFuncId() {
		return smgFuncId;
	}
	public void setSmgFuncId(Integer smgFuncId) {
		this.smgFuncId = smgFuncId;
	}
	public Integer getSmgId() {
		return smgId;
	}
	public void setSmgId(Integer smgId) {
		this.smgId = smgId;
	}
}
