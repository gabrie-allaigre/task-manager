package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.task.NormalTask;
import com.synaptix.taskmanager.engine.taskdefinition.INormalTaskDefinition;
import com.synaptix.taskmanager.model.ITaskObject;

public class SimpleNormalTask extends NormalTask implements ISimpleCommon {

	private ITaskObject taskObject;

	public SimpleNormalTask(INormalTaskDefinition normalTaskDefinition) {
		super(normalTaskDefinition);
	}

	@Override
	public void setTaskObject(ITaskObject taskObject) {
		this.taskObject = taskObject;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <G extends ITaskObject> G getTaskObject() {
		return (G) taskObject;
	}

	@Override
	public String toString() {
		return "SimpleNormalTask -> " + getNormalTaskDefinition().getCode();
	}
}
