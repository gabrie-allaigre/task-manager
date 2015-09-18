package com.talanlabs.taskmanager.engine.listener;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.talanlabs.taskmanager.model.ITaskCluster;

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
