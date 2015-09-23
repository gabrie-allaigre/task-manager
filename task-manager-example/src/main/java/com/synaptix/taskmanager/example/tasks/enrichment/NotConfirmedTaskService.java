package com.synaptix.taskmanager.example.tasks.enrichment;

import com.synaptix.taskmanager.engine.memory.SimpleNormalTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public class NotConfirmedTaskService extends AbstractTaskService {

	public NotConfirmedTaskService() {
		super(ServiceNature.MANUAL_ENRICHMENT);
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		if (!((SimpleNormalTask) task).<ICustomerOrder> getTaskObject().isConfirmed()) {
			return ExecutionResultBuilder.newBuilder().finished();
		} else {
			return ExecutionResultBuilder.newBuilder().notFinished();
		}
	}
}
