package com.synaptix.taskmanager.example.tasks.enrichment;

import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.manager.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public class INCTaskService extends AbstractTaskService {

	public INCTaskService() {
		super(ServiceNature.ENRICHMENT, ICustomerOrder.class);
	}

	@Override
	public IExecutionResult execute(ITask task) {
		return new ExecutionResultBuilder().finished();
	}
}
