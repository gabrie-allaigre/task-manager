package com.synaptix.taskmanager.example.component.task.enrichment;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.component.business.ICustomerOrder;

public class ReferenceTaskService extends AbstractTaskService {

	private final String ref;

	public ReferenceTaskService(String ref) {
		super();

		this.ref = ref;
	}

	@Override
	public IExecutionResult execute(IEngineContext context,ICommonTask task) {
		((SimpleSubTask) task).<ICustomerOrder> getTaskObject().setReference(ref);
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
