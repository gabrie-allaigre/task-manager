package com.synaptix.taskmanager.example.tasks.updatestatus;

import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.manager.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public class CANTaskService extends AbstractTaskService {

	public CANTaskService() {
		super(ServiceNature.UPDATE_STATUS, ICustomerOrder.class);
	}

	@Override
	public IExecutionResult execute(ITask task) {
		return new ExecutionResultBuilder().finished();
	}
}
