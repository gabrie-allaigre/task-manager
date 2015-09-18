package com.synaptix.taskmanager.simple;

import java.util.Date;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.model.domains.TaskStatus;

public class SimpleTask implements ITask {

	@Override
	public TaskStatus getTaskStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTaskStatus(TaskStatus taskStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public ServiceNature getNature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNature(ServiceNature nature) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getServiceCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServiceCode(String serviceCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCheckSkippable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCheckSkippable(boolean checkSkippable) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCheckError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCheckError(boolean checkError) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setErrorMessage(String errorMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getExecutantRole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExecutantRole(String executantRole) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getManagerRole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setManagerRole(String managerRole) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getNextStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNextStatus(String nextStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCheckGroup() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCheckGroup(boolean checkGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCheckTodoExecutantCreated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCheckTodoExecutantCreated(boolean checkTodoExecutantCreated) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCheckTodoManagerCreated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCheckTodoManagerCreated(boolean checkTodoManagerCreated) {
		// TODO Auto-generated method stub

	}

	@Override
	public LocalDateTime getFirstErrorDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFirstErrorDate(LocalDateTime firstErrorDateDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public Duration getTodoManagerDuration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTodoManagerDuration(Duration todoManagerDuration) {
		// TODO Auto-generated method stub

	}

	@Override
	public Date getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStartDate(Date startDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public Date getEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEndDate(Date endDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCheckUserValidation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCheckUserValidation(boolean checkUserValidation) {
		// TODO Auto-generated method stub

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
	public Class<? extends ITaskObject<?>> getTaskObjectClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTaskObjectClass(Class<? extends ITaskObject<?>> taskObjectClass) {
		// TODO Auto-generated method stub

	}

}
