package com.synaptix.taskmanager.engine.listener;

import java.util.EventListener;

import com.synaptix.taskmanager.engine.task.AbstractTask;

public interface ITaskCycleListener extends EventListener {

	public void onTodo(AbstractTask task);

	public void onCurrent(AbstractTask task);

	public void onNothing(AbstractTask task);

	public void onDone(AbstractTask task);

	public void onDelete(AbstractTask task);
}
