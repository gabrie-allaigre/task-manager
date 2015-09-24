package com.synaptix.taskmanager.example.tasks.enrichment;

import java.util.Calendar;

import com.synaptix.taskmanager.engine.memory.SimpleNormalTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.business.ICustomerOrder;

public class DateClosedTaskService extends AbstractTaskService {

	public DateClosedTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(AbstractTask task) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, 1);
		((SimpleNormalTask) task).<ICustomerOrder> getTaskObject().setDateClosed(c.getTime());
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
