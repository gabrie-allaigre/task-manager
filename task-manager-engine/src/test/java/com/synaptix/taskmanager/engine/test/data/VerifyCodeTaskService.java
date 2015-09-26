package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.memory.SimpleNormalTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class VerifyCodeTaskService extends AbstractTaskService {

	private final String code;

	public VerifyCodeTaskService(String code) {
		super();

		this.code = code;
	}

	@Override
	public IExecutionResult execute(IContext context,AbstractTask task) {
		if (code != null && code.equals(((SimpleNormalTask) task).<BusinessObject> getTaskObject().getCode())) {
			return ExecutionResultBuilder.newBuilder().noChanges().finished();
		}
		return ExecutionResultBuilder.newBuilder().notFinished();
	}

}
