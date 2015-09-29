package com.synaptix.taskmanager.engine.configuration.factory;

import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.engine.task.IGeneralTask;
import com.synaptix.taskmanager.engine.taskdefinition.ISubTaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.IGeneralTaskDefinition;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskFactory {

	/**
	 * Create a new Task cluster
	 * 
	 * @return
	 */
	ITaskCluster newTaskCluster();

	/**
	 * Create a new task
	 * 
	 * @return
	 */
	ISubTask newSubTask(ISubTaskDefinition subTaskDefinition);

	/**
	 * Create a update status task
	 * 
	 * @return
	 */
	IGeneralTask newGeneralTask(IGeneralTaskDefinition generalTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus);

}
