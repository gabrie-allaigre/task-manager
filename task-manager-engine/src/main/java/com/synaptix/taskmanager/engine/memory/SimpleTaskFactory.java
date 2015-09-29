package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.configuration.factory.AbstractTaskFactory;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.engine.task.IGeneralTask;
import com.synaptix.taskmanager.engine.taskdefinition.ISubTaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.IGeneralTaskDefinition;
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
	public IGeneralTask newGeneralTask(IGeneralTaskDefinition generalTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
		return new SimpleGeneralTask(generalTaskDefinition, taskObjectClass, currentStatus);
	}
}
