package com.synaptix.taskmanager.engine.task;

import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;

public abstract class AbstractTask {

	private final ITaskDefinition taskDefinition;

	public AbstractTask(ITaskDefinition taskDefinition) {
		super();

		this.taskDefinition = taskDefinition;
	}

	public final ITaskDefinition getTaskDefinition() {
		return this.taskDefinition;
	}
}
