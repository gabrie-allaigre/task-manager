package com.synaptix.taskmanager.example.tasks.enrichment;

import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.manager.AbstractTask;
import com.synaptix.taskmanager.manager.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.simple.SimpleNormalTask;

public class ReferenceTaskService extends AbstractTaskService {

	public ReferenceTaskService() {
		super(ServiceNature.ENRICHMENT);
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		((SimpleNormalTask) task).<ICustomerOrder> getTaskObject().setReference("Ma ref");
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
