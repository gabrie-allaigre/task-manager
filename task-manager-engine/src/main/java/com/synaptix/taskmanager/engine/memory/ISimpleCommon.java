package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.model.ITaskObject;

public interface ISimpleCommon {

	public void setTaskObject(ITaskObject<?> taskObject);

	public <G extends ITaskObject<?>> G getTaskObject();

}
