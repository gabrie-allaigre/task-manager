package com.synaptix.taskmanager.manager;

import java.net.URI;

import com.synaptix.taskmanager.manager.taskservice.ITaskService;
import com.synaptix.taskmanager.manager.taskservice.TodoDescriptorBuilder;
import com.synaptix.taskmanager.model.ITaskObject;

public abstract class AbstractTaskObjectManager<F extends ITaskObject<?>> implements ITaskObjectManager<F> {

	private final Class<F> taskObjectClass;

	private final ITaskService.ITodoDescriptor todoDescriptor;

	public AbstractTaskObjectManager(Class<F> taskObjectClass) {
		this(taskObjectClass, null, null);
	}

	public AbstractTaskObjectManager(Class<F> taskObjectClass, String todoCode, URI todoUri) {
		super();
		this.taskObjectClass = taskObjectClass;

		if (todoCode != null && todoUri != null) {
			this.todoDescriptor = new TodoDescriptorBuilder(todoCode, todoUri).description(getTodoDescription()).createToTodoTask(true).build();
		} else {
			this.todoDescriptor = null;
		}
	}

	@Override
	public final Class<F> getTaskObjectClass() {
		return taskObjectClass;
	}

	protected String getTodoDescription() {
		return null;
	}

	@Override
	public final ITaskService.ITodoDescriptor getDefaultTodoDescriptor() {
		return todoDescriptor;
	}
}
