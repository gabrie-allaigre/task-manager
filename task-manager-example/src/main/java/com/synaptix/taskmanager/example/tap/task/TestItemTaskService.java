package com.synaptix.taskmanager.example.tap.task;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class TestItemTaskService extends AbstractItemTaskService {

    public TestItemTaskService() {
        super("TEST");
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        return ExecutionResultBuilder.newBuilder().noChanges().finished();
    }
}
