package com.synaptix.taskmanager.engine.listener;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.synaptix.taskmanager.model.ITaskCluster;

public abstract class AbstractTaskCycleListener implements ITaskCycleListener {

    @Override
    public void onTodo(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task) {
    }

    @Override
    public void onCurrent(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task) {
    }

    @Override
    public void onNothing(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task) {
    }

    @Override
    public void onDone(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task) {
    }

    @Override
    public void onDelete(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task) {
    }
}
