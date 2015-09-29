package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.model.ITaskObject;

public abstract class AbstractSimpleCommonTask implements ICommonTask {

	private final String codeTaskDefinition;

	private ITaskObject taskObject;

	public AbstractSimpleCommonTask(String codeTaskDefinition) {
		super();

		this.codeTaskDefinition = codeTaskDefinition;
	}

	@Override
	public final String getCodeTaskDefinition() {
		return codeTaskDefinition;
	}

	public void setTaskObject(ITaskObject taskObject) {
		this.taskObject = taskObject;
	}

	public <G extends ITaskObject> G getTaskObject() {
		return (G)taskObject;
	}
}
