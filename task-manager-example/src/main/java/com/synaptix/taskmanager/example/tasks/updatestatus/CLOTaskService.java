package com.synaptix.taskmanager.example.tasks.updatestatus;

import java.util.Date;

import com.synaptix.taskmanager.engine.memory.SimpleUpdateStatusTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.business.ICustomerOrder;

public class CLOTaskService extends AbstractTaskService {

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
