package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.configuration.factory.AbstractTaskFactory;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public class SimpleTaskFactory extends AbstractTaskFactory {

    @Override
    public ITaskCluster newTaskCluster() {
        return new SimpleTaskCluster();
    }

    @Override
    public ISubTask newSubTask(String codeSubTaskDefinition) {
        return new SimpleSubTask(codeSubTaskDefinition);
    }

    @Override
    public IStatusTask newStatusTask(String codeStatusTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
        return new SimpleStatusTask(codeStatusTaskDefinition, taskObjectClass, currentStatus);
    }
}
