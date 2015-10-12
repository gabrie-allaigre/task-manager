package com.synaptix.taskmanager.example.component.task.updatestatus;

import com.synaptix.taskmanager.engine.memory.SimpleStatusTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.component.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.component.business.ICustomerOrder;

public class TCOTaskService extends AbstractTaskService {

    public TCOTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        ((SimpleStatusTask) task).<ICustomerOrder>getTaskObject().setStatus(CustomerOrderStatus.TCO);
        return ExecutionResultBuilder.newBuilder().finished();
    }
}
