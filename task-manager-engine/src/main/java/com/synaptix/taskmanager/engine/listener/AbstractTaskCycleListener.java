package com.synaptix.taskmanager.engine.listener;

import com.synaptix.taskmanager.engine.task.AbstractTask;

public abstract class AbstractTaskCycleListener implements ITaskCycleListener {

	@Override
	public void onTodo(AbstractTask task) {
	}

	@Override
	public void onCurrent(AbstractTask task) {
	}

	@Override
	public void onNothing(AbstractTask task) {
	}

	@Override
	public void onDone(AbstractTask task) {
	}

	@Override
	public void onDelete(AbstractTask task) {
	}
}
