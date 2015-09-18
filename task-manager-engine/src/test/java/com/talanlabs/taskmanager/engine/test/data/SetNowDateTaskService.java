package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.engine.memory.SimpleSubTask;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;

import java.util.Date;

public class SetNowDateTaskService extends AbstractTaskService {

    public SetNowDateTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        ((SimpleSubTask) task).<BusinessObject>getTaskObject().setDate(new Date());
        return ExecutionResultBuilder.newBuilder().finished();
    }
}
