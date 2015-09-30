package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.engine.configuration.factory.AbstractTaskFactory;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.example.jpa.model.Cluster;
import com.synaptix.taskmanager.example.jpa.model.IBusinessTaskObject;
import com.synaptix.taskmanager.example.jpa.model.Task;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public class JPATaskFactory extends AbstractTaskFactory {

	@Override
	public ITaskCluster newTaskCluster() {
		return new Cluster();
	}

	@Override
	public ISubTask newSubTask(String codeSubTaskDefinition) {
		Task task = new Task();
		task.setType(Task.Type.subTask);
		task.setCodeTaskDefinition(codeSubTaskDefinition);
		return task;
	}

	@Override
	public boolean isSubTask(ICommonTask task) {
		return Task.Type.subTask.equals(((Task) task).getType());
	}

	@Override
	public IStatusTask newStatusTask(String codeStatusTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
		Task task = new Task();
		task.setType(Task.Type.statusTask);
		task.setCodeTaskDefinition(codeStatusTaskDefinition);
		task.setBusinessTaskObjectClass((Class<? extends IBusinessTaskObject>) taskObjectClass);
		task.setCurrentStatus((String)currentStatus);
		return task;
	}

	@Override
	public boolean isStatusTask(ICommonTask task) {
		return Task.Type.statusTask.equals(((Task) task).getType());
	}
}
