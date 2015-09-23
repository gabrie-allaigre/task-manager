package com.synaptix.taskmanager.engine.task;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.taskmanager.engine.taskdefinition.INormalTaskDefinition;

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
