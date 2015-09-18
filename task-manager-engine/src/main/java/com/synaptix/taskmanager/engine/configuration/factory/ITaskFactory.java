package com.synaptix.taskmanager.engine.configuration.factory;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;

public interface ITaskFactory {

	public ITaskCluster newTaskCluster();

	public ITask newTask();

}
