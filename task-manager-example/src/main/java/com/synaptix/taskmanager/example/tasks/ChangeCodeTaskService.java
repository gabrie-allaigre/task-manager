package com.synaptix.taskmanager.example.tasks;

import com.synaptix.taskmanager.engine.memory.SimpleNormalTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.business.ICustomerOrder;

public class ChangeCodeTaskService extends AbstractTaskService {

	private final String newCode;

	public ChangeCodeTaskService(String newCode) {
		super();

		this.newCode = newCode;
	}

	@Override
	public IExecutionResult execute(IContext context,AbstractTask task) {
		((SimpleNormalTask) task).<ICustomerOrder> getTaskObject().setCustomerOrderNo(newCode);
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
