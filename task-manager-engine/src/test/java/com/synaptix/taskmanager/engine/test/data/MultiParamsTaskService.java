package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class MultiParamsTaskService extends AbstractTaskService {

	private final String newCode;

	public MultiParamsTaskService(String newCode,int entier,float rien,MyStatus status) {
		super();

		this.newCode = newCode;
	}

	@Override
	public IExecutionResult execute(IEngineContext context,ICommonTask task) {
		((SimpleSubTask) task).<BusinessObject> getTaskObject().setCode(newCode);
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
