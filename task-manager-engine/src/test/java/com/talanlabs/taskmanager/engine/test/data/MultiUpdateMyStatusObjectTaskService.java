package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class MultiUpdateMyStatusObjectTaskService extends AbstractTaskService {

    private final MyStatusObject status;

    public MultiUpdateMyStatusObjectTaskService(MyStatusObject status) {
        super();

        this.status = status;
    }

    public MyStatusObject getStatus() {
        return status;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        return ExecutionResultBuilder.newBuilder().finished();
    }
}
