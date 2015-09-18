package com.synaptix.taskmanager.model;

import org.joda.time.Duration;

public interface ITaskType {

	public String getCode();

	public String getServiceCode();

	public boolean isCheckSkippable();

	public String getExecutantRole();

	public String getManagerRole();

	public Duration getTodoManagerDuration();

	public int getResultDepth();

}
