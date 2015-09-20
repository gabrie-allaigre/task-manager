package com.synaptix.taskmanager.example.tasks.enrichment;

import java.util.Date;

import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.manager.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.simple.SimpleTask;

public class DateTaskService extends AbstractTaskService {

	public DateTaskService() {
		super(ServiceNature.ENRICHMENT, ICustomerOrder.class);
	}

	@Override
	public IExecutionResult execute(ITask task) {
		((SimpleTask) task).<ICustomerOrder> getTaskObject().setDate(new Date());
		return new ExecutionResultBuilder().finished();
	}
}
