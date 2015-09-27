package com.synaptix.taskmanager.example.tasks.updatestatus;

import com.synaptix.taskmanager.engine.memory.SimpleUpdateStatusTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.business.ICustomerOrder;

public class CANTaskService extends AbstractTaskService {

	public CANTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IEngineContext context,AbstractTask task) {
		ICustomerOrder customerOrder = ((SimpleUpdateStatusTask) task).<ICustomerOrder> getTaskObject();
		if (customerOrder.isCancelled()) {
			((SimpleUpdateStatusTask) task).<ICustomerOrder> getTaskObject().setStatus(CustomerOrderStatus.CAN);
			return ExecutionResultBuilder.newBuilder().finished();
		} else {
			return ExecutionResultBuilder.newBuilder().notFinished();
		}
	}
}
