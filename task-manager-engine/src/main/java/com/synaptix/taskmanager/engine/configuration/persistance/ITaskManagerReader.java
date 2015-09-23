package com.synaptix.taskmanager.engine.configuration.persistance;

import java.util.List;

import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskManagerReader {

	/**
	 * Find a task cluster by task object
	 * 
	 * @param taskObject
	 * @return
	 */
	public ITaskCluster findTaskClusterByTaskObject(ITaskObject<?> taskObject);

	/**
	 * Find all taskObjects by task cluster
	 * 
	 * note : used when taskCluster is not checkGraphCreated
	 * 
	 * @param taskCluster
	 * @return
	 */
	public List<ITaskObject<?>> findTaskObjectsByTaskCluster(ITaskCluster taskCluster);

	/**
	 * Find all currents tasks for task cluster
	 * 
	 * @param taskCluster
	 * @return
	 */
	public List<AbstractTask> findCurrentTasksByTaskCluster(ITaskCluster taskCluster);

}