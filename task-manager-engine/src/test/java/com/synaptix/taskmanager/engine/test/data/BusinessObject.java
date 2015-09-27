package com.synaptix.taskmanager.engine.test.data;

import java.util.Date;

import com.synaptix.taskmanager.model.ITaskObject;

public class BusinessObject implements ITaskObject {

	private String status;

	private String code;

	private Date date;

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
