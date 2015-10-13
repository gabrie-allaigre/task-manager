package com.synaptix.taskmanager.example.jpa.task;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class StopTaskService extends AbstractTaskService {

    public StopTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        return ExecutionResultBuilder.newBuilder().notFinished();
    }

}
