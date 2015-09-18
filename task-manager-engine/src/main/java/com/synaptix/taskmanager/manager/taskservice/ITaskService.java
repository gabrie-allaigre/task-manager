package com.synaptix.taskmanager.manager.taskservice;

import java.net.URI;
import java.util.Set;

import com.synaptix.component.model.IError;
import com.synaptix.component.model.IStackResult;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public interface ITaskService {

	/**
	 * Nature of Task
	 * 
	 * @return
	 */
	public ServiceNature getNature();

	/**
	 * Object kinds
	 * 
	 * @return
	 */
	public Class<? extends ITaskObject<?>> getObjectKinds();

	public void onTodo(ITask task);

	public void onCurrent(ITask task);

	public IExecutionResult execute(ITask task);

	public void onDone(ITask task);

	public void onSkipped(ITask task);

	public void onCanceled(ITask task);

	/**
	 * Create new todo descriptor
	 * 
	 * @return
	 */
	public ITodoDescriptor newTodoDescriptor(ITask task);

	public interface IExecutionResult {

		public boolean isFinished();

		public String getErrorMessage();

		public boolean hasErrors();

		public Set<IError> getErrors();

		public IStackResult getStackResult();

		public String getResultStatus();

		public String getResultDesc();

		public boolean mustStopAndRestartTaskManager();
	}

	public interface ITodoDescriptor {

		public URI getUri();

		public String getCode();

		public String getDescription();

		public boolean isCreateToTodoTask();

	}
}
