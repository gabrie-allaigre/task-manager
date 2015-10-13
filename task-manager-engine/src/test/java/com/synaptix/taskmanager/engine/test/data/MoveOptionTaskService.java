package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class MoveOptionTaskService extends AbstractTaskService {

    public MoveOptionTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        context.moveTaskObjectsToTaskCluster(((SimpleSubTask) task).<BusinessObject>getTaskObject().getOptionObject());
        return ExecutionResultBuilder.newBuilder().finished();
    }
}
