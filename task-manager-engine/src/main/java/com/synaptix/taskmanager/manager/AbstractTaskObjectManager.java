package com.synaptix.taskmanager.manager;

import com.synaptix.taskmanager.model.ITaskObject;

public abstract class AbstractTaskObjectManager<F extends ITaskObject<?>> implements ITaskObjectManager<F> {

	private final Class<F> taskObjectClass;

	public AbstractTaskObjectManager(Class<F> taskObjectClass) {
		super();
		this.taskObjectClass = taskObjectClass;
	}

	@Override
	public final Class<F> getTaskObjectClass() {
		return taskObjectClass;
	}
}
