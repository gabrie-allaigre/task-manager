package com.synaptix.taskmanager.example.tasks.updatestatus;

import com.synaptix.taskmanager.example.CustomerOrderStatus;
import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.manager.AbstractTask;
import com.synaptix.taskmanager.manager.taskservice.AbstractUpdateStatusTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.simple.SimpleUpdateStatusTask;

public class TCOTaskService extends AbstractUpdateStatusTaskService {

	public TCOTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		((SimpleUpdateStatusTask) task).<ICustomerOrder> getTaskObject().setStatus(CustomerOrderStatus.TCO);
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
