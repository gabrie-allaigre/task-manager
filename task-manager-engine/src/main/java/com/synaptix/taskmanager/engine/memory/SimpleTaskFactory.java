package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.configuration.factory.AbstractTaskFactory;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.taskdefinition.ISubTaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.IStatusTaskDefinition;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public class SimpleTaskFactory extends AbstractTaskFactory {

	@Override
	public ITaskCluster newTaskCluster() {
		return new SimpleTaskCluster();
	}

	@Override
	public ISubTask newSubTask(ISubTaskDefinition subTaskDefinition) {
		return new SimpleSubTask(subTaskDefinition);
	}

	@Override
	public IStatusTask newStatusTask(IStatusTaskDefinition statusTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
		return new SimpleStatusTask(statusTaskDefinition, taskObjectClass, currentStatus);
	}
}
