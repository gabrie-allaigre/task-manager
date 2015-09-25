package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.model.ITaskObject;

public interface ISimpleCommon {

	void setTaskObject(ITaskObject<?> taskObject);

	<G extends ITaskObject<?>> G getTaskObject();

}
