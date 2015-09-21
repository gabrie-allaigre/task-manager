package com.synaptix.taskmanager.manager.taskservice;

import com.synaptix.component.model.IStackResult;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public interface ITaskService {

	public ServiceNature getNature();

	public void onTodo(ITask task);

	public void onCurrent(ITask task);

	public IExecutionResult execute(ITask task);

	public void onNothing(ITask task);

	public void onDone(ITask task);

	public interface IExecutionResult {

		public boolean isFinished();

		public IStackResult getStackResult();

		public String getResultStatus();

		public String getResultDesc();

		public boolean mustStopAndRestartTaskManager();
	}
}
