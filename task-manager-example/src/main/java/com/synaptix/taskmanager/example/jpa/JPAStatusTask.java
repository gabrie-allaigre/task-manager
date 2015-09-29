package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.taskdefinition.IStatusTaskDefinition;
import com.synaptix.taskmanager.example.jpa.model.Todo;
import com.synaptix.taskmanager.model.ITaskObject;

public class JPAStatusTask extends AbstractJPACommonTask implements IStatusTask {

	private final Object currentStatus;

	public JPAStatusTask(IStatusTaskDefinition statusTaskDefinition, Object currentStatus) {
		super(statusTaskDefinition);

		this.currentStatus = currentStatus;
	}

	@Override
	public <F extends ITaskObject> Class<F> getTaskObjectClass() {
		return (Class<F>)Todo.class;
	}

	@Override
	public <E extends Object> E getCurrentStatus() {
		return (E)currentStatus;
	}

	@Override
	public String toString() {
		return "JPAStatusTask -> " + getCurrentStatus();
	}
}
