package com.synaptix.taskmanager.example.component.task;

import com.synaptix.taskmanager.engine.memory.SimpleStatusTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.component.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.component.business.ICustomerOrder;

public class MultiUpdateStatusTaskService extends AbstractTaskService {

    private final CustomerOrderStatus status;

    public MultiUpdateStatusTaskService(CustomerOrderStatus status) {
        super();

        this.status = status;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        ((SimpleStatusTask) task).<ICustomerOrder>getTaskObject().setStatus(status);
        return ExecutionResultBuilder.newBuilder().finished();
    }
}
