package com.synaptix.taskmanager.simple;

import com.synaptix.taskmanager.engine.configuration.factory.AbstractTaskFactory;
import com.synaptix.taskmanager.manager.NormalTask;
import com.synaptix.taskmanager.manager.UpdateStatusTask;
import com.synaptix.taskmanager.manager.taskdefinition.INormalTaskDefinition;
import com.synaptix.taskmanager.manager.taskdefinition.IUpdateStatusTaskDefinition;
import com.synaptix.taskmanager.model.ITaskCluster;

public class SimpleTaskFactory extends AbstractTaskFactory {

	@Override
	public ITaskCluster newTaskCluster() {
		return new SimpleTaskCluster();
	}

	@Override
	public NormalTask newNormalTask(INormalTaskDefinition normalTaskDefinition) {
		return new SimpleNormalTask(normalTaskDefinition);
	}

	@Override
	public UpdateStatusTask newUpdateStatusTask(IUpdateStatusTaskDefinition updateStatusTaskDefinition, Class<?> taskObjectClass, Object currentStatus, UpdateStatusTask previousUpdateStatusTask) {
		return new SimpleUpdateStatusTask(updateStatusTaskDefinition, taskObjectClass, currentStatus, previousUpdateStatusTask);
	}
}
