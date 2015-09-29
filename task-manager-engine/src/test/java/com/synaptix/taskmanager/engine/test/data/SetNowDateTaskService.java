package com.synaptix.taskmanager.engine.test.data;

import java.util.Date;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class SetNowDateTaskService extends AbstractTaskService {

	public SetNowDateTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IEngineContext context,ICommonTask task) {
		((SimpleSubTask) task).<BusinessObject> getTaskObject().setDate(new Date());
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
