package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.configuration.factory.AbstractTaskFactory;
import com.synaptix.taskmanager.engine.task.NormalTask;
import com.synaptix.taskmanager.engine.task.UpdateStatusTask;
import com.synaptix.taskmanager.engine.taskdefinition.INormalTaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.IUpdateStatusTaskDefinition;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

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
	public UpdateStatusTask newUpdateStatusTask(IUpdateStatusTaskDefinition updateStatusTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus,
			UpdateStatusTask previousUpdateStatusTask) {
		return new SimpleUpdateStatusTask(updateStatusTaskDefinition, taskObjectClass, currentStatus, previousUpdateStatusTask);
	}
}
