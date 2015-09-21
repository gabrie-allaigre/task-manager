package com.synaptix.taskmanager.manager.taskservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public abstract class AbstractTaskService implements ITaskService {

	private static final Log LOG = LogFactory.getLog(AbstractTaskService.class);

	private final ServiceNature nature;

	public AbstractTaskService(ServiceNature nature) {
		super();

		this.nature = nature;
	}

	@Override
	public final ServiceNature getNature() {
		return nature;
	}

	@Override
	public void onTodo(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onTodo " + task);
		}
	}

	@Override
	public void onCurrent(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onCurrent " + task);
		}
	}

	@Override
	public void onNothing(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onNothing " + task);
		}
	}

	@Override
	public void onDone(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onDone " + task);
		}
	}
}
