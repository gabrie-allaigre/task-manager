package com.synaptix.taskmanager.engine.taskservice;

import com.synaptix.taskmanager.model.domains.ServiceNature;

public abstract class AbstractUpdateStatusTaskService extends AbstractTaskService {

	public AbstractUpdateStatusTaskService() {
		super(ServiceNature.UPDATE_STATUS);
	}
}
