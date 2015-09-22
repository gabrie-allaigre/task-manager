package com.synaptix.taskmanager.engine.configuration.graph;

import java.util.List;

import com.synaptix.taskmanager.manager.graph.IStatusGraph;

public interface IStatusGraphRegistry {

	/**
	 * Get next status graphs for task object and current status
	 * 
	 * Ex : A->(B,C) currentStatus is A then next is B and C
	 * 
	 * @param taskObjectClass
	 * @param currentStatus
	 * @return
	 */
	public List<IStatusGraph> getNextStatusGraphsByTaskObjectType(Class<?> taskObjectClass, Object currentStatus);

}
