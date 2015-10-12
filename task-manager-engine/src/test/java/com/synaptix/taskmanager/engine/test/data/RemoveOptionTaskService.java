package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class RemoveOptionTaskService extends AbstractTaskService {

	public RemoveOptionTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IEngineContext context, ICommonTask task) {
		context.removeTaskObjectsFromTaskCluster(((SimpleSubTask) task).<BusinessObject>getTaskObject().getOptionObject());
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
