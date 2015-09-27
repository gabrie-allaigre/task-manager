package com.synaptix.taskmanager.example.tasks;

import com.synaptix.taskmanager.engine.memory.SimpleUpdateStatusTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.business.ICustomerOrder;

public class MultiUpdateStatusTaskService extends AbstractTaskService {

	private final CustomerOrderStatus status;

	public MultiUpdateStatusTaskService(CustomerOrderStatus status) {
		super();

		this.status = status;
	}

	@Override
	public IExecutionResult execute(IEngineContext context,AbstractTask task) {
		((SimpleUpdateStatusTask) task).<ICustomerOrder> getTaskObject().setStatus(status);
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
