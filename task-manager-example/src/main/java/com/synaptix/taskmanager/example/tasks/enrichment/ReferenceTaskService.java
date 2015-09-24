package com.synaptix.taskmanager.example.tasks.enrichment;

import com.synaptix.taskmanager.engine.memory.SimpleNormalTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.business.ICustomerOrder;

public class ReferenceTaskService extends AbstractTaskService {

	private final String ref;

	public ReferenceTaskService(String ref) {
		super();

		this.ref = ref;
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		((SimpleNormalTask) task).<ICustomerOrder> getTaskObject().setReference(ref);
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
