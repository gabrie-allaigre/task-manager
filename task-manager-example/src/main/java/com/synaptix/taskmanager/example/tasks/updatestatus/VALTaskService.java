package com.synaptix.taskmanager.example.tasks.updatestatus;

import com.synaptix.taskmanager.example.CustomerOrderStatus;
import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.manager.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.simple.SimpleTask;

public class VALTaskService extends AbstractTaskService {

	public VALTaskService() {
		super(ServiceNature.UPDATE_STATUS);
	}

	@Override
	public IExecutionResult execute(ITask task) {
		ICustomerOrder customerOrder = ((SimpleTask) task).<ICustomerOrder> getTaskObject();
		if (customerOrder.isConfirmed()) {
			((SimpleTask) task).<ICustomerOrder> getTaskObject().setStatus(CustomerOrderStatus.VAL);
			return new ExecutionResultBuilder().finished();
		} else {
			return new ExecutionResultBuilder().notFinished();
		}
	}
}
