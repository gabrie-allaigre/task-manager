package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;

public class BugTaskService extends AbstractTaskService {

	public BugTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IContext context,AbstractTask task) {
		throw new RuntimeException("BUG");
	}
}
