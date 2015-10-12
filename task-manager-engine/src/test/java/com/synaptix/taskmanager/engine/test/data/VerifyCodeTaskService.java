package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class VerifyCodeTaskService extends AbstractTaskService {

	private final String code;

	public VerifyCodeTaskService(String code) {
		super();

		this.code = code;
	}

	@Override
	public IExecutionResult execute(IEngineContext context,ICommonTask task) {
		if (code != null && code.equals(((SimpleSubTask) task).<AbstractCommonObject> getTaskObject().getCode())) {
			return ExecutionResultBuilder.newBuilder().noChanges().finished();
		}
		return ExecutionResultBuilder.newBuilder().notFinished();
	}

}
