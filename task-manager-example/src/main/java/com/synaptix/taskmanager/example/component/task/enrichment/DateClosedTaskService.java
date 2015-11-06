package com.synaptix.taskmanager.example.component.task.enrichment;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.component.business.ICustomerOrder;

import java.util.Calendar;

public class DateClosedTaskService extends AbstractTaskService {

    public DateClosedTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 1);
        ((SimpleSubTask) task).<ICustomerOrder>getTaskObject().setDateClosed(c.getTime());
        return ExecutionResultBuilder.newBuilder().finished();
    }
}
