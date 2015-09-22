package com.synaptix.taskmanager.manager.taskservice;

import com.synaptix.taskmanager.manager.AbstractTask;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public interface ITaskService {

	public ServiceNature getNature();

	public void onTodo(ITask task);

	public void onCurrent(ITask task);

	public IExecutionResult execute(AbstractTask task);

	public void onNothing(ITask task);

	public void onDone(ITask task);

	public interface IExecutionResult {

		public boolean isFinished();

		public Object getResult();

		public boolean mustStopAndRestartTaskManager();
	}
}
