package com.synaptix.taskmanager.engine.configuration.transform;

import java.util.List;

import com.synaptix.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.synaptix.taskmanager.engine.task.NormalTask;

public interface ITaskChainCriteriaTransform {

	public List<NormalTask> transformeToTasks(ITaskManagerConfiguration taskManagerConfiguration, String taskChainCriteria);

}
