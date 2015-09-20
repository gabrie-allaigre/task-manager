package com.synaptix.taskmanager.engine.configuration.factory;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

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
	 * @param taskCluster
	 * @param taskObject
	 * @return
	 */
	public ITask newTask(ITaskCluster taskCluster, ITaskObject<?> taskObject);

}
