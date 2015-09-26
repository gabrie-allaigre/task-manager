package com.synaptix.taskmanager.engine.manager;

import com.synaptix.taskmanager.model.ITaskObject;

public abstract class AbstractTaskObjectManager<E extends Object,F extends ITaskObject<E>> implements ITaskObjectManager<E,F> {

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
