package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.synaptix.taskmanager.model.ITaskObject;

public abstract class AbstractSimpleCommonTask implements ICommonTask {

	private final ITaskDefinition taskDefinition;

	private ITaskObject taskObject;

	public AbstractSimpleCommonTask(ITaskDefinition taskDefinition) {
		super();

		this.taskDefinition = taskDefinition;
	}

	@Override
	public final ITaskDefinition getTaskDefinition() {
		return taskDefinition;
	}

	public void setTaskObject(ITaskObject taskObject) {
		this.taskObject = taskObject;
	}

	public <G extends ITaskObject> G getTaskObject() {
		return (G)taskObject;
	}
}
