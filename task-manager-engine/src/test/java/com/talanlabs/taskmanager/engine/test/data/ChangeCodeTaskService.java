package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.engine.memory.SimpleSubTask;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class ChangeCodeTaskService extends AbstractTaskService {

    private final String newCode;

    public ChangeCodeTaskService(String newCode) {
        super();

        this.newCode = newCode;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        ((SimpleSubTask) task).<AbstractCommonObject>getTaskObject().setCode(newCode);
        return ExecutionResultBuilder.newBuilder().finished();
    }
}
