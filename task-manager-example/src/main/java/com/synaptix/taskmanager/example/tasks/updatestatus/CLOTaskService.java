package com.synaptix.taskmanager.example.tasks.updatestatus;

import java.util.Date;

import com.synaptix.taskmanager.example.CustomerOrderStatus;
import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.manager.AbstractTask;
import com.synaptix.taskmanager.manager.taskservice.AbstractUpdateStatusTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.simple.SimpleUpdateStatusTask;

public class CLOTaskService extends AbstractUpdateStatusTaskService {

	public CLOTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		ICustomerOrder customerOrder = ((SimpleUpdateStatusTask) task).<ICustomerOrder> getTaskObject();
		if (customerOrder.getDateClosed() != null && customerOrder.getDateClosed().before(new Date())) {
			((SimpleUpdateStatusTask) task).<ICustomerOrder> getTaskObject().setStatus(CustomerOrderStatus.CLO);
			return ExecutionResultBuilder.newBuilder().finished();
		} else {
			return ExecutionResultBuilder.newBuilder().notFinished();
		}
	}
}
