package com.synaptix.taskmanager.example.tasks.enrichment;

import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.manager.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.simple.SimpleTask;

public class ReferenceTaskService extends AbstractTaskService {

	public ReferenceTaskService() {
		super(ServiceNature.ENRICHMENT);
	}

	@Override
	public IExecutionResult execute(ITask task) {
		((SimpleTask) task).<ICustomerOrder> getTaskObject().setReference("Ma ref");
		return new ExecutionResultBuilder().finished();
	}
}
