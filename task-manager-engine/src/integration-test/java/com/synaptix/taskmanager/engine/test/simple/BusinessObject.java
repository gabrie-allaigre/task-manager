package com.synaptix.taskmanager.engine.test.simple;

import com.synaptix.taskmanager.model.ITaskObject;

public class BusinessObject implements ITaskObject<String> {

	private String status;

	private String code;

	@Override
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
