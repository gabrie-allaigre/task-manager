package com.synaptix.taskmanager.example.component.task.updatestatus;

import java.util.Date;

import com.synaptix.taskmanager.engine.memory.SimpleGeneralTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.component.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.component.business.ICustomerOrder;

public class CLOTaskService extends AbstractTaskService {

	public CLOTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IEngineContext context,ICommonTask task) {
		ICustomerOrder customerOrder = ((SimpleGeneralTask) task).<ICustomerOrder> getTaskObject();
		if (customerOrder.getDateClosed() != null && customerOrder.getDateClosed().before(new Date())) {
			((SimpleGeneralTask) task).<ICustomerOrder> getTaskObject().setStatus(CustomerOrderStatus.CLO);
			return ExecutionResultBuilder.newBuilder().finished();
		} else {
			return ExecutionResultBuilder.newBuilder().notFinished();
		}
	}
}
