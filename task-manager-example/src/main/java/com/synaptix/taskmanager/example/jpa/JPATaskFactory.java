package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.engine.configuration.factory.ITaskFactory;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.engine.task.IGeneralTask;
import com.synaptix.taskmanager.engine.taskdefinition.ISubTaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.IGeneralTaskDefinition;
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
	public IGeneralTask newGeneralTask(IGeneralTaskDefinition generalTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
		return new JPAGeneralTask(generalTaskDefinition,taskObjectClass,currentStatus);
	}
}
