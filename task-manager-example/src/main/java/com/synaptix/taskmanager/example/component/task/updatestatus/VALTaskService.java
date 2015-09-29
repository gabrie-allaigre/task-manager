package com.synaptix.taskmanager.example.component.task.updatestatus;

import com.synaptix.taskmanager.engine.memory.SimpleGeneralTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.component.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.component.business.ICustomerOrder;

public class VALTaskService extends AbstractTaskService {

	public VALTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IEngineContext context, ICommonTask task) {
		ICustomerOrder customerOrder = ((SimpleGeneralTask) task).<ICustomerOrder>getTaskObject();
		if (customerOrder.isConfirmed()) {
			((SimpleGeneralTask) task).<ICustomerOrder>getTaskObject().setStatus(CustomerOrderStatus.VAL);
			return ExecutionResultBuilder.newBuilder().finished();
		} else {
			return ExecutionResultBuilder.newBuilder().notFinished();
		}
	}
}
