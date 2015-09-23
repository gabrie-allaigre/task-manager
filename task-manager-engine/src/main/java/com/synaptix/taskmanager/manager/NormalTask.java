package com.synaptix.taskmanager.manager;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.taskmanager.manager.taskdefinition.INormalTaskDefinition;

public class NormalTask extends AbstractTask {

	private final List<AbstractTask> nextTasks;

	public NormalTask(INormalTaskDefinition taskDefinition) {
		super(taskDefinition);

		this.nextTasks = new ArrayList<AbstractTask>();
	}

	public final INormalTaskDefinition getNormalTaskDefinition() {
		return (INormalTaskDefinition) getTaskDefinition();
	}

	public final List<AbstractTask> getNextTasks() {
		return nextTasks;
	}
}
