package com.synaptix.taskmanager.model;

import com.synaptix.taskmanager.model.domains.TaskStatus;

public interface ITask {

	public TaskStatus getTaskStatus();

	public void setTaskStatus(TaskStatus taskStatus);

	public <F extends ITaskObject<?>> Class<F> getTaskObjectClass();

	public <F extends ITaskObject<?>> void setTaskObjectClass(Class<F> taskObjectClass);

	public String getServiceCode();

	public void setServiceCode(String serviceCode);

	// Result

	public String getResultStatus();

	public void setResultStatus(String resultStatus);

	public String getResultDesc();

	public void setResultDesc(String resultDesc);

	public String getResultDetail();

	public void setResultDetail(String resultDetail);

}
