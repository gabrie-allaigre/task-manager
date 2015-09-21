package com.synaptix.taskmanager.example.tasks.enrichment;

import java.util.Calendar;

import com.synaptix.taskmanager.example.ICustomerOrder;
import com.synaptix.taskmanager.manager.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.simple.SimpleTask;

public class DateClosedTaskService extends AbstractTaskService {

	public DateClosedTaskService() {
		super(ServiceNature.ENRICHMENT);
	}

	@Override
	public IExecutionResult execute(ITask task) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, 1);
		((SimpleTask) task).<ICustomerOrder> getTaskObject().setDateClosed(c.getTime());
		return new ExecutionResultBuilder().finished();
	}
}
