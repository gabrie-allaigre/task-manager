package com.synaptix.taskmanager.example.tasks.updatestatus;

import com.synaptix.taskmanager.example.CustomerOrderStatus;
import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.manager.AbstractTask;
import com.synaptix.taskmanager.manager.taskservice.AbstractUpdateStatusTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.simple.SimpleUpdateStatusTask;

public class VALTaskService extends AbstractUpdateStatusTaskService {

	public VALTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		ICustomerOrder customerOrder = ((SimpleUpdateStatusTask) task).<ICustomerOrder> getTaskObject();
		if (customerOrder.isConfirmed()) {
			((SimpleUpdateStatusTask) task).<ICustomerOrder> getTaskObject().setStatus(CustomerOrderStatus.VAL);
			return ExecutionResultBuilder.newBuilder().finished();
		} else {
			return ExecutionResultBuilder.newBuilder().notFinished();
		}
	}
}
