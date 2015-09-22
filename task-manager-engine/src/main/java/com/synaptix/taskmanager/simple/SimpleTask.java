package com.synaptix.taskmanager.simple;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.TaskStatus;

public class SimpleTask implements ITask {

	private ITaskObject<?> taskObject;

	private TaskStatus taskStatus;

	private String serviceCode;

	private Class<? extends ITaskObject<?>> taskObjectClass;

	public <F extends ITaskObject<?>> F getTaskObject() {
		return (F) taskObject;
	}

	public <F extends ITaskObject<?>> void setTaskObject(F taskObject) {
		this.taskObject = taskObject;
	}

	@Override
	public TaskStatus getTaskStatus() {
		return this.taskStatus;
	}

	@Override
	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	@Override
	public String getServiceCode() {
		return this.serviceCode;
	}

	@Override
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	@Override
	public String getResultStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResultStatus(String resultStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getResultDesc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResultDesc(String resultDesc) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getResultDetail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResultDetail(String resultDetail) {
		// TODO Auto-generated method stub

	}

	@Override
	public <F extends ITaskObject<?>> Class<F> getTaskObjectClass() {
		return (Class<F>) this.taskObjectClass;
	}

	@Override
	public <F extends ITaskObject<?>> void setTaskObjectClass(Class<F> taskObjectClass) {
		this.taskObjectClass = taskObjectClass;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
