package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;

public class BugTaskService extends AbstractTaskService {

    public BugTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        throw new RuntimeException("BUG");
    }
}
