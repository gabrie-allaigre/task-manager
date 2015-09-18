package com.talanlabs.taskmanager.engine.memory;

import com.talanlabs.taskmanager.engine.configuration.factory.AbstractTaskFactory;
import com.talanlabs.taskmanager.engine.task.IStatusTask;
import com.talanlabs.taskmanager.engine.task.ISubTask;
import com.talanlabs.taskmanager.model.ITaskCluster;
import com.talanlabs.taskmanager.model.ITaskObject;

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
