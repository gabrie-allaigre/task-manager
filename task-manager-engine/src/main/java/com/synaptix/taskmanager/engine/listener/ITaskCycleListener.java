package com.synaptix.taskmanager.engine.listener;

import java.util.EventListener;

import com.synaptix.taskmanager.engine.task.ICommonTask;

public interface ITaskCycleListener extends EventListener {

	void onTodo(ICommonTask task);

	void onCurrent(ICommonTask task);

	void onNothing(ICommonTask task);

	void onDone(ICommonTask task);

	void onDelete(ICommonTask task);
}
