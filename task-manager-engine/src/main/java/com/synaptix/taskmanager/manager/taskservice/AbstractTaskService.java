package com.synaptix.taskmanager.manager.taskservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public abstract class AbstractTaskService implements ITaskService {

	private static final Log LOG = LogFactory.getLog(AbstractTaskService.class);

	private final ServiceNature nature;

	private final Class<? extends ITaskObject<?>> objectType;

	public AbstractTaskService(ServiceNature nature, Class<? extends ITaskObject<?>> objectType) {
		super();

		this.nature = nature;
		this.objectType = objectType;
	}

	@Override
	public final ServiceNature getNature() {
		return nature;
	}

	@Override
	public final Class<? extends ITaskObject<?>> getObjectKinds() {
		return objectType;
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
	public void onDone(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onDone " + task);
		}
	}

	@Override
	public void onSkipped(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onSkipped " + task);
		}
	}

	@Override
	public void onCanceled(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onCanceled " + task);
		}
	}

	@Override
	public ITodoDescriptor newTodoDescriptor(ITask task) {
		return null;
	}
}
