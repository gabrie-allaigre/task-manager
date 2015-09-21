package com.synaptix.taskmanager.manager.taskservice;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;

import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import de.jkeylockmanager.manager.ReturnValueLockCallback;

public abstract class AbstractLockTaskService<F> extends AbstractTaskService {

	private final KeyLockManager keyLockManager;

	public AbstractLockTaskService(ServiceNature nature) {
		super(nature);

		this.keyLockManager = KeyLockManagers.newLock();
	}

	/**
	 * Return lock key, not NULL
	 * 
	 * @param task
	 * @return
	 */
	protected abstract String getLockKey(ITask task);

	@Override
	public final IExecutionResult execute(final ITask task) {
		return keyLockManager.executeLocked(getLockKey(task), new ReturnValueLockCallback<IExecutionResult>() {
			@Override
			public IExecutionResult doInLock() throws Exception {
				return executeInLock(task);
			}
		});
	}

	protected abstract IExecutionResult executeInLock(ITask task);

}
