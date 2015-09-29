package com.synaptix.taskmanager.engine.listener;

import com.synaptix.taskmanager.engine.task.ICommonTask;

public abstract class AbstractTaskCycleListener implements ITaskCycleListener {

	@Override
	public void onTodo(ICommonTask task) {
	}

	@Override
	public void onCurrent(ICommonTask task) {
	}

	@Override
	public void onNothing(ICommonTask task) {
	}

	@Override
	public void onDone(ICommonTask task) {
	}

	@Override
	public void onDelete(ICommonTask task) {
	}
}
