package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.synaptix.taskmanager.example.jpa.model.Task;

public abstract class AbstractJPACommonTask implements ICommonTask {

	private final ITaskDefinition taskDefinition;

	private Task task;

	public AbstractJPACommonTask(ITaskDefinition taskDefinition) {
		super();

		this.taskDefinition = taskDefinition;
	}

	@Override
	public final ITaskDefinition getTaskDefinition() {
		return taskDefinition;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
