package com.synaptix.taskmanager.manager;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskObjectManager<F extends ITaskObject<?>> {

	public Class<F> getTaskObjectClass();

	/**
	 * Get a task chain criteria for group task
	 */
	public String getTaskChainCriteria(ITask task);

}
