package com.synaptix.taskmanager.simple;

import com.synaptix.taskmanager.manager.UpdateStatusTask;
import com.synaptix.taskmanager.manager.taskdefinition.IUpdateStatusTaskDefinition;
import com.synaptix.taskmanager.model.ITaskObject;

public class SimpleUpdateStatusTask extends UpdateStatusTask {

	private ITaskObject<?> taskObject;

	public SimpleUpdateStatusTask(IUpdateStatusTaskDefinition updateStatusTaskDefinition, Class<? extends ITaskObject<?>> taskObjectClass, Object currentStatus,
			UpdateStatusTask previousUpdateStatusTask) {
		super(updateStatusTaskDefinition, taskObjectClass, currentStatus, previousUpdateStatusTask);
	}

	public void setTaskObject(ITaskObject<?> taskObject) {
		this.taskObject = taskObject;
	}

	public <G extends ITaskObject<?>> G getTaskObject() {
		return (G) taskObject;
	}

	@Override
	public String toString() {
		return "SimpleUpdateStatusTask -> " + getCurrentStatus();
	}
}
