package com.synaptix.taskmanager.example.component.task.enrichment;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.component.business.ICustomerOrder;

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
