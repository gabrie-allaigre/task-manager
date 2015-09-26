package com.synaptix.taskmanager.engine.manager;

import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.task.UpdateStatusTask;
import com.synaptix.taskmanager.model.ITaskObject;

import java.util.List;

public interface ITaskObjectManager<E extends Object,F extends ITaskObject<E>> {

	Class<F> getTaskObjectClass();

	/**
	 * Get next status graphs for task object and current status
	 *
	 * Ex : A->(B,C) currentStatus is A then next is B and C
	 *
	 * @param updateStatusTask
	 * @param currentStatus
	 * @return
	 */
	List<IStatusGraph<E>> getNextStatusGraphsByTaskObjectType(UpdateStatusTask updateStatusTask, E currentStatus);

	String getTaskChainCriteria(UpdateStatusTask updateStatusTask, E currentStatus, E nextStatus);

}
