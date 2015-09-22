package com.synaptix.taskmanager.manager;

import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskObjectManager<F extends ITaskObject<?>> {

	public Class<F> getTaskObjectClass();

	public String getTaskChainCriteria(UpdateStatusTask updateStatusTask, Object currentStatus, Object nextStatus);

}
