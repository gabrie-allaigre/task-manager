package com.talanlabs.taskmanager.example.component.task.updatestatus;

import com.talanlabs.taskmanager.engine.memory.SimpleStatusTask;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.talanlabs.taskmanager.example.component.business.CustomerOrderStatus;
import com.talanlabs.taskmanager.example.component.business.ICustomerOrder;

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
