package com.synaptix.taskmanager.example.tasks.enrichment;

import com.synaptix.taskmanager.engine.memory.SimpleNormalTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.business.ICustomerOrder;

public class NotConfirmedTaskService extends AbstractTaskService {

	public NotConfirmedTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IContext context,AbstractTask task) {
		if (!((SimpleNormalTask) task).<ICustomerOrder> getTaskObject().isConfirmed()) {
			return ExecutionResultBuilder.newBuilder().noChanges().finished();
		} else {
			return ExecutionResultBuilder.newBuilder().notFinished();
		}
	}
}
