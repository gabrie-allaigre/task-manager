package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class MultiUpdateMyStatusObjectTaskService extends AbstractTaskService {

	private final MyStatusObject status;

	public MultiUpdateMyStatusObjectTaskService(MyStatusObject status) {
		super();

		this.status = status;
	}

	public MyStatusObject getStatus() {
		return status;
	}

	@Override
	public IExecutionResult execute(IEngineContext context,ICommonTask task) {
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
