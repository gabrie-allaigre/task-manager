package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class MoveToNewOptionTaskService extends AbstractTaskService {

	public MoveToNewOptionTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IEngineContext context, ICommonTask task) {
		context.moveTaskObjectsToNewTaskCluster(((SimpleSubTask) task).<BusinessObject>getTaskObject().getOptionObject());
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
