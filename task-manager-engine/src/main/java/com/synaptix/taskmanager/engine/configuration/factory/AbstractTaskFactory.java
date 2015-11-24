package com.synaptix.taskmanager.engine.configuration.factory;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;

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
