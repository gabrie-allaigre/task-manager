package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.task.NormalTask;
import com.synaptix.taskmanager.engine.taskdefinition.INormalTaskDefinition;
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

	@Override
	public String toString() {
		return "SimpleNormalTask -> " + getNormalTaskDefinition().getCode();
	}
}
