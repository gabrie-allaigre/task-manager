package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.taskdefinition.IStatusTaskDefinition;
import com.synaptix.taskmanager.model.ITaskObject;

public class JPAStatusTask extends AbstractJPACommonTask implements IStatusTask {

	private final Class<? extends ITaskObject> taskObjectClass;

	private final Object currentStatus;

	public JPAStatusTask(IStatusTaskDefinition statusTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
		super(statusTaskDefinition);

		this.taskObjectClass = taskObjectClass;
		this.currentStatus = currentStatus;
	}

	@Override
	public <F extends ITaskObject> Class<F> getTaskObjectClass() {
		return (Class<F>)taskObjectClass;
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
