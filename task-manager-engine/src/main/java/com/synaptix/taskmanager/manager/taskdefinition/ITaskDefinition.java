package com.synaptix.taskmanager.manager.taskdefinition;

import com.synaptix.taskmanager.manager.taskservice.ITaskService;

public interface ITaskDefinition {

	public String getCode();

	public ITaskService getTaskService();

	public int getResultDepth();

}
