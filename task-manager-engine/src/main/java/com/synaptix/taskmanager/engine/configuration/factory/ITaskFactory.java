package com.synaptix.taskmanager.engine.configuration.factory;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;

public interface ITaskFactory {

	/**
	 * Create a new Task cluster
	 * 
	 * @return
	 */
	public ITaskCluster newTaskCluster();

	/**
	 * Create a new task
	 * 
	 * @return
	 */
	public ITask newTask();

}
