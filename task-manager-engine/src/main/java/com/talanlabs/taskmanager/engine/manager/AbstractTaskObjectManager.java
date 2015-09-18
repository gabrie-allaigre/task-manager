package com.talanlabs.taskmanager.engine.manager;

import com.talanlabs.taskmanager.model.ITaskObject;

public abstract class AbstractTaskObjectManager<E, F extends ITaskObject> implements ITaskObjectManager<E, F> {

    private final Class<F> taskObjectClass;

    public AbstractTaskObjectManager(Class<F> taskObjectClass) {
        super();
        this.taskObjectClass = taskObjectClass;
    }

    @Override
    public final Class<F> getTaskObjectClass() {
        return taskObjectClass;
    }
}
