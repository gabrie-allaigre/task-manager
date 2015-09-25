package com.synaptix.taskmanager.engine.configuration.graph;

import java.util.List;

import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.task.UpdateStatusTask;
import com.synaptix.taskmanager.model.ITaskObject;

public interface IStatusGraphRegistry {

	/**
	 * Get next status graphs for task object and current status
	 * 
	 * Ex : A->(B,C) currentStatus is A then next is B and C
	 * 
	 * @param taskObjectClass
	 * @param updateStatusTask
	 * @param currentStatus
	 * @return
	 */
	public <E extends Object, F extends ITaskObject<E>> List<IStatusGraph<E>> getNextStatusGraphsByTaskObjectType(Class<F> taskObjectClass, UpdateStatusTask updateStatusTask, E currentStatus);

}
