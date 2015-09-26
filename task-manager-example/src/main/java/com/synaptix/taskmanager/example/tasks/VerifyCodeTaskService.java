package com.synaptix.taskmanager.example.tasks;

import com.synaptix.taskmanager.engine.memory.SimpleNormalTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.business.ICustomerOrder;

public class VerifyCodeTaskService extends AbstractTaskService {

	private final String code;

	public VerifyCodeTaskService(String code) {
		super();

		this.code = code;
	}

	@Override
	public IExecutionResult execute(IContext context,AbstractTask task) {
		if (code != null && code.equals(((SimpleNormalTask) task).<ICustomerOrder> getTaskObject().getCustomerOrderNo())) {
			return ExecutionResultBuilder.newBuilder().noChanges().finished();
		}
		return ExecutionResultBuilder.newBuilder().notFinished();
	}

}
