package com.synaptix.taskmanager.example.tasks.updatestatus;

import com.synaptix.taskmanager.engine.memory.SimpleUpdateStatusTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.business.ICustomerOrder;

public class TCOTaskService extends AbstractTaskService {

	public TCOTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		((SimpleUpdateStatusTask) task).<ICustomerOrder> getTaskObject().setStatus(CustomerOrderStatus.TCO);
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
