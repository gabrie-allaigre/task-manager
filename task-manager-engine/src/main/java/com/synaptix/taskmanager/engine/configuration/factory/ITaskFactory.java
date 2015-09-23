package com.synaptix.taskmanager.engine.configuration.factory;

import com.synaptix.taskmanager.engine.task.NormalTask;
import com.synaptix.taskmanager.engine.task.UpdateStatusTask;
import com.synaptix.taskmanager.engine.taskdefinition.INormalTaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.IUpdateStatusTaskDefinition;
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
	 * @return
	 */
	public NormalTask newNormalTask(INormalTaskDefinition normalTaskDefinition);

	/**
	 * Create a update status task
	 * 
	 * @return
	 */
	public UpdateStatusTask newUpdateStatusTask(IUpdateStatusTaskDefinition updateStatusTaskDefinition, Class<? extends ITaskObject<?>> taskObjectClass, Object currentStatus,
			UpdateStatusTask previousUpdateStatusTask);

}
