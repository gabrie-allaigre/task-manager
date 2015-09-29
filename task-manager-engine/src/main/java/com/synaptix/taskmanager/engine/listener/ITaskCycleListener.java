package com.synaptix.taskmanager.engine.listener;

import java.util.EventListener;

import com.synaptix.taskmanager.engine.task.ICommonTask;

public interface ITaskCycleListener extends EventListener {

	public void onTodo(ICommonTask task);

	public void onCurrent(ICommonTask task);

	public void onNothing(ICommonTask task);

	public void onDone(ICommonTask task);

	public void onDelete(ICommonTask task);
}
