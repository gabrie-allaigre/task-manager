package com.synaptix.taskmanager.engine.manager;

import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.model.ITaskObject;

import java.util.List;

public interface ITaskObjectManager<E,F extends ITaskObject> {

	/**
	 * Get a class of taskObject
	 *
	 * @return a class
	 */
	Class<F> getTaskObjectClass();

	/**
	 * Get a initial status for task object
	 *
	 * @param taskObject a task object
	 * @return a current status for task object
	 */
	E getInitialStatus(F taskObject);

	/**
	 * Get next status graphs for task object and current status
	 *
	 * Ex : A->(B,C) currentStatus is A then next is B and C
	 *
	 * @param statusTask
	 * @param currentStatus a current status
	 * @return a graph for next status
	 */
	List<IStatusGraph<E>> getNextStatusGraphsByTaskObjectType(IStatusTask statusTask, E currentStatus);

	/**
	 * Get a rule with task definition between status for sub tasks
	 *
	 * ex : A=>(B,C)
	 *
	 * @param statusTask
	 * @param currentStatus current status
	 * @param nextStatus next status
	 * @return a rule
	 */
	String getTaskChainCriteria(IStatusTask statusTask, E currentStatus, E nextStatus);

}
