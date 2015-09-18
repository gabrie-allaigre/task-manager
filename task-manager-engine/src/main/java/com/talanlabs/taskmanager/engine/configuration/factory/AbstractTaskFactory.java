package com.talanlabs.taskmanager.engine.configuration.factory;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.task.IStatusTask;
import com.talanlabs.taskmanager.engine.task.ISubTask;

public abstract class AbstractTaskFactory implements ITaskFactory {

    @Override
    public boolean isSubTask(ICommonTask commonTask) {
        return commonTask instanceof ISubTask;
    }

    @Override
    public boolean isStatusTask(ICommonTask commonTask) {
        return commonTask instanceof IStatusTask;
    }
}
