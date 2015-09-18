package com.synaptix.taskmanager.model;

public interface ITaskClusterDependency {

	public Class<? extends ITaskObject<?>> getObjectType();

	public void setObjectType(Class<? extends ITaskObject<?>> objectType);

}
