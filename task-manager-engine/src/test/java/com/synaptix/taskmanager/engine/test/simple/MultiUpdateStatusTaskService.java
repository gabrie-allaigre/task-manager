package com.synaptix.taskmanager.engine.test.simple;

import com.synaptix.taskmanager.engine.memory.SimpleUpdateStatusTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractUpdateStatusTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class MultiUpdateStatusTaskService extends AbstractUpdateStatusTaskService {

	private final String status;

	public MultiUpdateStatusTaskService(String status) {
		super();

		this.status = status;
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		((SimpleUpdateStatusTask) task).<BusinessObject> getTaskObject().setStatus(status);
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
