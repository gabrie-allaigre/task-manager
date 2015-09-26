package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;

public class NullTaskService extends AbstractTaskService {

	public NullTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IContext context,AbstractTask task) {
		return null;
	}
}
