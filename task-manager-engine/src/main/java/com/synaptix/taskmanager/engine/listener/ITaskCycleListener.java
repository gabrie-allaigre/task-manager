package com.synaptix.taskmanager.engine.listener;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.synaptix.taskmanager.model.ITaskCluster;

import java.util.EventListener;

public interface ITaskCycleListener extends EventListener {

	void onTodo(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task);

	void onCurrent(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task);

	void onNothing(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task);

	void onDone(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task);

	void onDelete(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task);
}
