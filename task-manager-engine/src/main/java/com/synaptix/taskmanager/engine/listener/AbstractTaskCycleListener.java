package com.synaptix.taskmanager.engine.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.taskmanager.engine.task.AbstractTask;

public abstract class AbstractTaskCycleListener implements ITaskCycleListener {

	private static final Log LOG = LogFactory.getLog(AbstractTaskCycleListener.class);

	@Override
	public void onTodo(AbstractTask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - onTodo " + task);
		}
	}

	@Override
	public void onCurrent(AbstractTask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - onCurrent " + task);
		}
	}

	@Override
	public void onNothing(AbstractTask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - onNothing " + task);
		}
	}

	@Override
	public void onDone(AbstractTask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - onDone " + task);
		}
	}

	@Override
	public void onDelete(AbstractTask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - onDelete " + task);
		}
	}
}
