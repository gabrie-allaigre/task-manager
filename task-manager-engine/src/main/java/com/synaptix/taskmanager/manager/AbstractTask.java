package com.synaptix.taskmanager.manager;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.taskmanager.manager.taskdefinition.ITaskDefinition;

public abstract class AbstractTask {

	private final ITaskDefinition taskDefinition;

	private final List<AbstractTask> nextTasks;

	public AbstractTask(ITaskDefinition taskDefinition) {
		super();

		this.taskDefinition = taskDefinition;

		this.nextTasks = new ArrayList<AbstractTask>();
	}

	public final ITaskDefinition getTaskDefinition() {
		return this.taskDefinition;
	}

	public final List<AbstractTask> getNextTasks() {
		return nextTasks;
	}
}
