package com.synaptix.taskmanager.manager;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.taskmanager.manager.taskdefinition.INormalTaskDefinition;

public class NormalTask extends AbstractTask {

	public NormalTask(INormalTaskDefinition taskDefinition) {
		super(taskDefinition);
	}

	public final INormalTaskDefinition getNormalTaskDefinition() {
		return (INormalTaskDefinition) getTaskDefinition();
	}

	public List<AbstractTask> getNextTasks() {
		return new ArrayList<AbstractTask>();
	}
}
