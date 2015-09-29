package com.synaptix.taskmanager.engine.configuration.transform;

import java.util.List;
import java.util.Map;

import com.synaptix.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.synaptix.taskmanager.engine.task.ISubTask;

public interface ITaskChainCriteriaTransform {

	/**
	 * Trasnform chain to tasks
	 * @param taskManagerConfiguration
	 * @param taskChainCriteria
	 * @return
	 */
	IResult transformeToTasks(ITaskManagerConfiguration taskManagerConfiguration, String taskChainCriteria);

	interface IResult {

		List<ISubTask> getNewSubTasks();

		List<ISubTask> getNextSubTasks();

		Map<ISubTask,List<ISubTask>> getLinkNextTasksMap();

	}
}
