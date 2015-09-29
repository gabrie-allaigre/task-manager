package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.engine.configuration.factory.ITaskFactory;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.taskdefinition.ISubTaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.IStatusTaskDefinition;
import com.synaptix.taskmanager.example.jpa.model.Cluster;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public class JPATaskFactory implements ITaskFactory {
	@Override
	public ITaskCluster newTaskCluster() {
		return new Cluster();
	}

	@Override
	public ISubTask newSubTask(ISubTaskDefinition subTaskDefinition) {
		return new JPASubTask(subTaskDefinition);
	}

	@Override
	public IStatusTask newStatusTask(IStatusTaskDefinition statusTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
		return new JPAStatusTask(statusTaskDefinition,currentStatus);
	}
}
