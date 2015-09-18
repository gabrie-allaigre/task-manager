package com.talanlabs.taskmanager.engine.taskservice;

import com.talanlabs.taskmanager.engine.listener.AbstractTaskCycleListener;

public abstract class AbstractTaskService extends AbstractTaskCycleListener implements ITaskService {

    public AbstractTaskService() {
        super();
    }
}
