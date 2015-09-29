package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.engine.task.IGeneralTask;
import com.synaptix.taskmanager.engine.taskdefinition.IGeneralTaskDefinition;
import com.synaptix.taskmanager.model.ITaskObject;

public class JPAGeneralTask extends AbstractJPACommonTask implements IGeneralTask {

	private final Class<? extends ITaskObject> taskObjectClass;

	private final Object currentStatus;

	public JPAGeneralTask(IGeneralTaskDefinition generalTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
		super(generalTaskDefinition);

		this.taskObjectClass = taskObjectClass;
		this.currentStatus = currentStatus;
	}

	@Override
	public Class<? extends ITaskObject> getTaskObjectClass() {
		return taskObjectClass;
	}

	@Override
	public Object getCurrentStatus() {
		return currentStatus;
	}

	@Override
	public String toString() {
		return "JPAGeneralTask -> " + getCurrentStatus();
	}
}
