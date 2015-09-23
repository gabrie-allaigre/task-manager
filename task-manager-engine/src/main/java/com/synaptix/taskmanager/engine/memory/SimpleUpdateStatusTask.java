package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.task.UpdateStatusTask;
import com.synaptix.taskmanager.engine.taskdefinition.IUpdateStatusTaskDefinition;
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

	@SuppressWarnings("unchecked")
	public <G extends ITaskObject<?>> G getTaskObject() {
		return (G) taskObject;
	}

	@Override
	public String toString() {
		return "SimpleUpdateStatusTask -> " + getCurrentStatus();
	}
}