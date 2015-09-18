package com.talanlabs.taskmanager.jpa.test.data;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;

public class NullTaskService extends AbstractTaskService {

    public NullTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        return null;
    }
}
