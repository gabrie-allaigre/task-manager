package com.synaptix.taskmanager.engine.test.data;

import java.util.Date;

import com.synaptix.taskmanager.engine.memory.SimpleNormalTask;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class SetNowDateTaskService extends AbstractTaskService {

	public SetNowDateTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IContext context,AbstractTask task) {
		((SimpleNormalTask) task).<BusinessObject> getTaskObject().setDate(new Date());
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
