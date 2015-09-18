package com.synaptix.taskmanager.manager;

import com.synaptix.taskmanager.manager.taskservice.ITaskService;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskObjectManager<F extends ITaskObject<?>> {

	public Class<F> getTaskObjectClass();

	/**
	 * Get a task chain criteria for group task
	 */
	public String getTaskChainCriteria(ITask task);

	/**
	 * Get a executant for the task
	 */
	public Object getExecutant(ITask task);

	/**
	 * Get a manager for the task
	 */
	public Object getManager(ITask task);

	/**
	 * Get a default todo descriptor
	 */
	public ITaskService.ITodoDescriptor getDefaultTodoDescriptor();

}
