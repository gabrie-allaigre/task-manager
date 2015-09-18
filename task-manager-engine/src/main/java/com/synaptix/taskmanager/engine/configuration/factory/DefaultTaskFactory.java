package com.synaptix.taskmanager.engine.configuration.factory;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;

public class DefaultTaskFactory extends AbstractTaskFactory {

	@Override
	public ITaskCluster newTaskCluster() {
		return null;
	}

	@Override
	public ITask newTask() {
		return null;
	}
}
