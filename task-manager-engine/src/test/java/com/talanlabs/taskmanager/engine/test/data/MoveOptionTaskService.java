package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.engine.memory.SimpleSubTask;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;

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
