package com.synaptix.taskmanager.example.tasks;

import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class StopTaskService extends AbstractTaskService {

	public StopTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		return ExecutionResultBuilder.newBuilder().notFinished();
	}

}
