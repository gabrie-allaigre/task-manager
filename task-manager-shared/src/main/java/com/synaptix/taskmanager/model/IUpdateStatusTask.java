package com.synaptix.taskmanager.model;

public interface IUpdateStatusTask extends ITask {

	public String getStatus();

	public void setStatus(String status);

	public IUpdateStatusTask getPreviousUpdateStatusTask();

	public void setPreviousUpdateStatusTask(IUpdateStatusTask previousUpdateStatusTask);

}
