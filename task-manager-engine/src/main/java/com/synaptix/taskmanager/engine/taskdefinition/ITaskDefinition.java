package com.synaptix.taskmanager.engine.taskdefinition;

import com.synaptix.taskmanager.engine.taskservice.ITaskService;

public interface ITaskDefinition {

	public String getCode();

	public ITaskService getTaskService();

}
