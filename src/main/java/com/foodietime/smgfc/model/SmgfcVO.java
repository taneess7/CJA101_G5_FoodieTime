package com.foodietime.smgfc.model;

import java.io.Serializable;

public class SmgfcVO implements Serializable{
	private Integer smgFuncId;
	private String smgFunc;
	public Integer getSmgFuncId() {
		return smgFuncId;
	}
	public void setSmgFuncId(Integer smgFuncId) {
		this.smgFuncId = smgFuncId;
	}
	public String getSmgFunc() {
		return smgFunc;
	}
	public void setSmgFunc(String smgFunc) {
		this.smgFunc = smgFunc;
	}
	
}
