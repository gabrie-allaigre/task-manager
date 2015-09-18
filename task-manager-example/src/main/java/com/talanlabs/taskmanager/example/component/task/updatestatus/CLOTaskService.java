package com.talanlabs.taskmanager.example.component.task.updatestatus;

import com.talanlabs.taskmanager.engine.memory.SimpleStatusTask;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.talanlabs.taskmanager.example.component.business.CustomerOrderStatus;
import com.talanlabs.taskmanager.example.component.business.ICustomerOrder;

import java.util.Date;

public class CLOTaskService extends AbstractTaskService {

    public CLOTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        ICustomerOrder customerOrder = ((SimpleStatusTask) task).<ICustomerOrder>getTaskObject();
        if (customerOrder.getDateClosed() != null && customerOrder.getDateClosed().before(new Date())) {
            ((SimpleStatusTask) task).<ICustomerOrder>getTaskObject().setStatus(CustomerOrderStatus.CLO);
            return ExecutionResultBuilder.newBuilder().finished();
        } else {
            return ExecutionResultBuilder.newBuilder().notFinished();
        }
    }
}
