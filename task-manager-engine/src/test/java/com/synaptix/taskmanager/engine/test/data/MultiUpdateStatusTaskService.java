package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.memory.SimpleUpdateStatusTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class MultiUpdateStatusTaskService extends AbstractTaskService {

	private final String status;

	public MultiUpdateStatusTaskService(String status) {
		super();

		this.status = status;
	}

	@Override
	public IExecutionResult execute(IEngineContext context,AbstractTask task) {
		((SimpleUpdateStatusTask) task).<BusinessObject> getTaskObject().setStatus(status);
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
