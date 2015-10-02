package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.model.ITaskObject;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSimpleCommonTask implements ICommonTask {

	public enum Status {
		TODO, CURRENT, DONE, CANCEL
	}

	private final String codeTaskDefinition;

	private final List<ICommonTask> previousTasks;

	private final List<ICommonTask> nextTasks;

	private ITaskObject taskObject;

	private Status status;

	public AbstractSimpleCommonTask(String codeTaskDefinition) {
		super();

		this.codeTaskDefinition = codeTaskDefinition;

		this.previousTasks = new ArrayList<>();
		this.nextTasks = new ArrayList<>();
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

	public final List<ICommonTask> getPreviousTasks() {
		return previousTasks;
	}

	public final List<ICommonTask> getNextTasks() {
		return nextTasks;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
