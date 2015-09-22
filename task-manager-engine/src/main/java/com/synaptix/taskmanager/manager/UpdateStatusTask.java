package com.synaptix.taskmanager.manager;

import com.synaptix.taskmanager.manager.taskdefinition.IUpdateStatusTaskDefinition;

public class UpdateStatusTask extends AbstractTask {

	private final Class<?> taskObjectClass;

	private final Object currentStatus;

	private final UpdateStatusTask previousUpdateStatusTask;

	public UpdateStatusTask(IUpdateStatusTaskDefinition updateStatusTaskDefinition, Class<?> taskObjectClass, Object currentStatus, UpdateStatusTask previousUpdateStatusTask) {
		super(updateStatusTaskDefinition);

		this.taskObjectClass = taskObjectClass;
		this.currentStatus = currentStatus;
		this.previousUpdateStatusTask = previousUpdateStatusTask;
	}

	public final IUpdateStatusTaskDefinition getUpdateStatusTaskDefinition() {
		return (IUpdateStatusTaskDefinition) getTaskDefinition();
	}

	public final Class<?> getTaskObjectClass() {
		return taskObjectClass;
	}

	public final Object getCurrentStatus() {
		return currentStatus;
	}

	public final UpdateStatusTask getPreviousUpdateStatusTask() {
		return previousUpdateStatusTask;
	}
}
