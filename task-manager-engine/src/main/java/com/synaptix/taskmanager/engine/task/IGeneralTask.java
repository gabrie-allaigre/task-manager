package com.synaptix.taskmanager.engine.task;

import com.synaptix.taskmanager.model.ITaskObject;

public interface IGeneralTask extends ICommonTask {

	Class<? extends ITaskObject> getTaskObjectClass();

	Object getCurrentStatus();

}
