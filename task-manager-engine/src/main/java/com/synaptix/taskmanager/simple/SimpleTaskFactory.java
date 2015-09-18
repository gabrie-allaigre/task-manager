package com.synaptix.taskmanager.simple;

import com.synaptix.taskmanager.engine.configuration.factory.AbstractTaskFactory;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;

public class SimpleTaskFactory extends AbstractTaskFactory {

	@Override
	public ITaskCluster newTaskCluster() {
		return new SimpleTaskCluster();
	}

	@Override
	public ITask newTask() {
		return new SimpleTask();
	}

}
