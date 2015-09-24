package com.synaptix.taskmanager.example.tasks.enrichment;

import com.synaptix.taskmanager.engine.memory.SimpleNormalTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.business.ICustomerOrder;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public class ReferenceTaskService extends AbstractTaskService {

	private final String ref;

	public ReferenceTaskService(String ref) {
		super(ServiceNature.ENRICHMENT);

		this.ref = ref;
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		((SimpleNormalTask) task).<ICustomerOrder> getTaskObject().setReference(ref);
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
