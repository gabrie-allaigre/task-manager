package com.synaptix.taskmanager.manager.taskdefinition;

import org.joda.time.Duration;

import com.synaptix.taskmanager.manager.taskservice.ITaskService;

public interface ITaskDefinition {

	public String getCode();

	public ITaskService getTaskService();

	public boolean isCheckSkippable();

	public String getExecutantRole();

	public String getManagerRole();

	public Duration getTodoManagerDuration();

	public int getResultDepth();

}
