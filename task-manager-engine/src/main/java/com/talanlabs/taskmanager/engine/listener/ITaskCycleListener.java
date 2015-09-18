package com.talanlabs.taskmanager.engine.listener;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.talanlabs.taskmanager.model.ITaskCluster;

import java.util.EventListener;

public interface ITaskCycleListener extends EventListener {

    void onTodo(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task);

    void onCurrent(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task);

    void onNothing(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task);

    void onDone(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task);

    void onDelete(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task);
}
