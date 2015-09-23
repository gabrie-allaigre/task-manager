package com.synaptix.taskmanager.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.taskmanager.manager.taskdefinition.IUpdateStatusTaskDefinition;
import com.synaptix.taskmanager.model.ITaskObject;

public class UpdateStatusTask extends AbstractTask {

	private final Class<? extends ITaskObject<?>> taskObjectClass;

	private final Object currentStatus;

	private final UpdateStatusTask previousUpdateStatusTask;

	private final Map<Object, List<? extends AbstractTask>> otherStatusTasksMap;

	public UpdateStatusTask(IUpdateStatusTaskDefinition updateStatusTaskDefinition, Class<? extends ITaskObject<?>> taskObjectClass, Object currentStatus, UpdateStatusTask previousUpdateStatusTask) {
		super(updateStatusTaskDefinition);

		this.taskObjectClass = taskObjectClass;
		this.currentStatus = currentStatus;
		this.previousUpdateStatusTask = previousUpdateStatusTask;

		this.otherStatusTasksMap = new HashMap<Object, List<? extends AbstractTask>>();
	}

	public final IUpdateStatusTaskDefinition getUpdateStatusTaskDefinition() {
		return (IUpdateStatusTaskDefinition) getTaskDefinition();
	}

	public final Class<? extends ITaskObject<?>> getTaskObjectClass() {
		return taskObjectClass;
	}

	public final Object getCurrentStatus() {
		return currentStatus;
	}

	public final UpdateStatusTask getPreviousUpdateStatusTask() {
		return previousUpdateStatusTask;
	}

	public final Map<Object, List<? extends AbstractTask>> getOtherStatusTasksMap() {
		return otherStatusTasksMap;
	}
}
