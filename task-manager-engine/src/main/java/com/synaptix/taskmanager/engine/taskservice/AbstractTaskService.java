package com.synaptix.taskmanager.engine.taskservice;

import com.synaptix.taskmanager.engine.listener.AbstractTaskCycleListener;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public abstract class AbstractTaskService extends AbstractTaskCycleListener implements ITaskService {

	private final ServiceNature nature;

	public AbstractTaskService(ServiceNature nature) {
		super();

		this.nature = nature;
	}

	@Override
	public final ServiceNature getNature() {
		return nature;
	}
}
