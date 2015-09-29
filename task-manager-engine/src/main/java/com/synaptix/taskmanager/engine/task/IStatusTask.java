package com.synaptix.taskmanager.engine.task;

import com.synaptix.taskmanager.model.ITaskObject;

public interface IStatusTask extends ICommonTask {

	/**
	 * Get class of task object for task
	 *
	 * @param <F> type of task object
	 * @return class of task object
	 */
	<F extends ITaskObject> Class<F> getTaskObjectClass();

	/**
	 * Get status for task
	 *
	 * @param <E> type of status
	 * @return status
	 */
	<E extends Object> E getCurrentStatus();

}
