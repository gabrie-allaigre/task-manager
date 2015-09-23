package com.synaptix.taskmanager.manager;

import com.synaptix.taskmanager.manager.taskdefinition.ITaskDefinition;

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
