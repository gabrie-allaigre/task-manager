package com.synaptix.taskmanager.engine.test.simple;

import com.synaptix.taskmanager.model.ITaskObject;

public class BusinessObject implements ITaskObject<String> {

	private String status;

	@Override
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
