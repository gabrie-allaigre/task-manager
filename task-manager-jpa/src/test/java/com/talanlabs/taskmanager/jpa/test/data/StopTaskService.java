package com.talanlabs.taskmanager.jpa.test.data;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class StopTaskService extends AbstractTaskService {

    public StopTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        return ExecutionResultBuilder.newBuilder().notFinished();
    }

}
