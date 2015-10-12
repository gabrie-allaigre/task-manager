package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class StartOptionTaskService extends AbstractTaskService {

	public StartOptionTaskService() {
		super();
	}

	@Override
	public IExecutionResult execute(IEngineContext context, ICommonTask task) {
		OptionObject oo = ((SimpleSubTask) task).<BusinessObject>getTaskObject().getOptionObject();
		if (oo == null) {
			oo = new OptionObject();
			((SimpleSubTask) task).<BusinessObject>getTaskObject().setOptionObject(oo);
		}
		context.startEngine(oo);
		return ExecutionResultBuilder.newBuilder().finished();
	}
}
