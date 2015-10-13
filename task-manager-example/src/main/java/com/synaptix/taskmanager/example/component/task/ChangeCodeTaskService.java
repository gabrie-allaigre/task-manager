package com.synaptix.taskmanager.example.component.task;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.component.business.ICustomerOrder;

public class ChangeCodeTaskService extends AbstractTaskService {

    private final String newCode;

    public ChangeCodeTaskService(String newCode) {
        super();

        this.newCode = newCode;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        ((SimpleSubTask) task).<ICustomerOrder>getTaskObject().setCustomerOrderNo(newCode);
        return ExecutionResultBuilder.newBuilder().finished();
    }
}
