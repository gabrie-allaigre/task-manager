package com.synaptix.taskmanager.example.tasks.updatestatus;

import com.synaptix.taskmanager.example.CustomerOrderStatus;
import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.manager.AbstractTask;
import com.synaptix.taskmanager.manager.taskservice.AbstractUpdateStatusTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.simple.SimpleUpdateStatusTask;

public class CANTaskService extends AbstractUpdateStatusTaskService {

	public CANTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		ICustomerOrder customerOrder = ((SimpleUpdateStatusTask) task).<ICustomerOrder> getTaskObject();
		if (customerOrder.isCancelled()) {
			((SimpleUpdateStatusTask) task).<ICustomerOrder> getTaskObject().setStatus(CustomerOrderStatus.CAN);
			return ExecutionResultBuilder.newBuilder().finished();
		} else {
			return ExecutionResultBuilder.newBuilder().notFinished();
		}
	}
}
