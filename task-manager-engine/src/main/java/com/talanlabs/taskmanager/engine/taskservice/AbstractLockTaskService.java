package com.talanlabs.taskmanager.engine.taskservice;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;

public abstract class AbstractLockTaskService extends AbstractTaskService {

    private final KeyLockManager keyLockManager;

    public AbstractLockTaskService() {
        super();

        this.keyLockManager = KeyLockManagers.newLock();
    }

    /**
     * Return lock key, not NULL
     *
     * @param task task link with service
     * @return key
     */
    protected abstract String getLockKey(ICommonTask task);

    @Override
    public final IExecutionResult execute(final IEngineContext context, final ICommonTask task) {
        return keyLockManager.executeLocked(getLockKey(task), () -> executeInLock(context, task));
    }

    protected abstract IExecutionResult executeInLock(IEngineContext context, ICommonTask task);

}
