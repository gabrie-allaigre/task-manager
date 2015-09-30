package com.synaptix.taskmanager.jpa.test.data;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;

public class BugTaskService extends AbstractTaskService {

	public BugTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IEngineContext context,ICommonTask task) {
		throw new RuntimeException("BUG");
	}
}
