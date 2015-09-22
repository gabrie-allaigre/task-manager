package com.synaptix.taskmanager.simple;

import com.synaptix.taskmanager.manager.NormalTask;
import com.synaptix.taskmanager.manager.taskdefinition.INormalTaskDefinition;
import com.synaptix.taskmanager.model.ITaskObject;

public class SimpleNormalTask extends NormalTask {

	private ITaskObject<?> taskObject;

	public SimpleNormalTask(INormalTaskDefinition normalTaskDefinition) {
		super(normalTaskDefinition);
	}

	public <E extends ITaskObject<?>> void setTaskObject(E taskObject) {
		this.taskObject = taskObject;
	}

	@SuppressWarnings("unchecked")
	public <E extends ITaskObject<?>> E getTaskObject() {
		return (E) taskObject;
	}
}
