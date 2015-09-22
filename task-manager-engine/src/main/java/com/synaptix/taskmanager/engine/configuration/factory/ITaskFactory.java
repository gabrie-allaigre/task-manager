package com.synaptix.taskmanager.engine.configuration.factory;

import com.synaptix.taskmanager.manager.NormalTask;
import com.synaptix.taskmanager.manager.UpdateStatusTask;
import com.synaptix.taskmanager.manager.taskdefinition.INormalTaskDefinition;
import com.synaptix.taskmanager.manager.taskdefinition.IUpdateStatusTaskDefinition;
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
	public NormalTask newNormalTask(INormalTaskDefinition normalTaskDefinition);

	/**
	 * Create a update status task
	 * 
	 * @return
	 */
	public UpdateStatusTask newUpdateStatusTask(IUpdateStatusTaskDefinition updateStatusTaskDefinition, Class<?> taskObjectClass, Object currentStatus, UpdateStatusTask previousUpdateStatusTask);

}
