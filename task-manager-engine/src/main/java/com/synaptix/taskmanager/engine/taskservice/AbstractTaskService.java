package com.synaptix.taskmanager.engine.taskservice;

import com.synaptix.taskmanager.engine.listener.AbstractTaskCycleListener;

public abstract class AbstractTaskService extends AbstractTaskCycleListener implements ITaskService {

	public AbstractTaskService() {
		super();
	}
}
