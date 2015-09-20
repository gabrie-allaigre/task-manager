package com.synaptix.taskmanager.simple;

import com.synaptix.taskmanager.engine.configuration.factory.AbstractTaskFactory;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public class SimpleTaskFactory extends AbstractTaskFactory {

	@Override
	public ITaskCluster newTaskCluster() {
		return new SimpleTaskCluster();
	}

	@Override
	public ITask newTask(ITaskCluster taskCluster, ITaskObject<?> taskObject) {
		SimpleTask simpleTask = new SimpleTask();
		simpleTask.setTaskObject(taskObject);
		return simpleTask;
	}

}
