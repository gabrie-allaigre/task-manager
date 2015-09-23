package com.synaptix.taskmanager.engine.taskservice;

import com.synaptix.taskmanager.engine.listener.ITaskCycleListener;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public interface ITaskService extends ITaskCycleListener {

	public ServiceNature getNature();

	public IExecutionResult execute(AbstractTask task);

	public interface IExecutionResult {

		public boolean isFinished();

		public Object getResult();

		public boolean mustStopAndRestartTaskManager();
	}
}
