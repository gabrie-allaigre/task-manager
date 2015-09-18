package com.synaptix.taskmanager.engine;

import java.util.List;

import com.synaptix.taskmanager.model.ITask;
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
	 * Find current tasks for cluster
	 * 
	 * @param taskCluster
	 * @return
	 */
	public List<ITask> findCurrentTasksForCluster(ITaskCluster taskCluster);

	public List<ITask> selectNextTodoToCurrentTasks(ITask task);

}
