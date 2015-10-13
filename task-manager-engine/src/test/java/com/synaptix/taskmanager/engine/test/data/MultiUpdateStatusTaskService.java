package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.memory.SimpleStatusTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class MultiUpdateStatusTaskService extends AbstractTaskService {

    private final String status;

    public MultiUpdateStatusTaskService(String status) {
        super();

        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        ((SimpleStatusTask) task).<AbstractCommonObject>getTaskObject().setStatus(status);
        return ExecutionResultBuilder.newBuilder().finished();
    }
}
