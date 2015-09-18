package com.talanlabs.taskmanager.example.component.task;

import com.talanlabs.taskmanager.engine.memory.SimpleStatusTask;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.talanlabs.taskmanager.example.component.business.CustomerOrderStatus;
import com.talanlabs.taskmanager.example.component.business.ICustomerOrder;

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
