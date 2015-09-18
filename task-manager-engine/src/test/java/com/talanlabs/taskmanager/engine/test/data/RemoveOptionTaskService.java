package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.engine.memory.SimpleSubTask;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;

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
