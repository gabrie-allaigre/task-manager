package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.engine.memory.SimpleSubTask;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class VerifyCodeTaskService extends AbstractTaskService {

    private final String code;

    public VerifyCodeTaskService(String code) {
        super();

        this.code = code;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        if (code != null && code.equals(((SimpleSubTask) task).<AbstractCommonObject>getTaskObject().getCode())) {
            return ExecutionResultBuilder.newBuilder().noChanges().finished();
        }
        return ExecutionResultBuilder.newBuilder().notFinished();
    }

}
