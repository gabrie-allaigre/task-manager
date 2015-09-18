package com.synaptix.taskmanager.model;

import java.util.Date;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.model.domains.TaskStatus;

public interface ITask {

	public TaskStatus getTaskStatus();

	public void setTaskStatus(TaskStatus taskStatus);

	public ServiceNature getNature();

	public void setNature(ServiceNature nature);

	public String getServiceCode();

	public void setServiceCode(String serviceCode);

	public boolean isCheckSkippable();

	public void setCheckSkippable(boolean checkSkippable);

	public boolean isCheckError();

	public void setCheckError(boolean checkError);

	public String getErrorMessage();

	public void setErrorMessage(String errorMessage);

	public String getExecutantRole();

	public void setExecutantRole(String executantRole);

	public String getManagerRole();

	public void setManagerRole(String managerRole);

	public String getNextStatus();

	public void setNextStatus(String nextStatus);

	public boolean isCheckGroup();

	public void setCheckGroup(boolean checkGroup);

	public boolean isCheckTodoExecutantCreated();

	public void setCheckTodoExecutantCreated(boolean checkTodoExecutantCreated);

	public boolean isCheckTodoManagerCreated();

	public void setCheckTodoManagerCreated(boolean checkTodoManagerCreated);

	public LocalDateTime getFirstErrorDate();

	public void setFirstErrorDate(LocalDateTime firstErrorDateDate);

	public Duration getTodoManagerDuration();

	public void setTodoManagerDuration(Duration todoManagerDuration);

	public Date getStartDate();

	public void setStartDate(Date startDate);

	public Date getEndDate();

	public void setEndDate(Date endDate);

	/**
	 * Set to true when the user has manually validated the task.
	 */
	public boolean isCheckUserValidation();

	public void setCheckUserValidation(boolean checkUserValidation);

	public String getResultStatus();

	public void setResultStatus(String resultStatus);

	public String getResultDesc();

	public void setResultDesc(String resultDesc);

	public String getResultDetail();

	public void setResultDetail(String resultDetail);

	public Class<? extends ITaskObject<?>> getTaskObjectClass();

	public void setTaskObjectClass(Class<? extends ITaskObject<?>> taskObjectClass);

}
