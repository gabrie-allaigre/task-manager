package com.synaptix.taskmanager.manager;

import com.synaptix.taskmanager.manager.taskdefinition.INormalTaskDefinition;

public class NormalTask extends AbstractTask {

	public NormalTask(INormalTaskDefinition taskDefinition) {
		super(taskDefinition);
	}

	public final INormalTaskDefinition getNormalTaskDefinition() {
		return (INormalTaskDefinition) getTaskDefinition();
	}
}
