package com.talanlabs.taskmanager.example.component.task.enrichment;

import com.talanlabs.taskmanager.engine.memory.SimpleSubTask;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.talanlabs.taskmanager.example.component.business.ICustomerOrder;

public class NotConfirmedTaskService extends AbstractTaskService {

    public NotConfirmedTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        if (!((SimpleSubTask) task).<ICustomerOrder>getTaskObject().isConfirmed()) {
            return ExecutionResultBuilder.newBuilder().noChanges().finished();
        } else {
            return ExecutionResultBuilder.newBuilder().notFinished();
        }
    }
}
