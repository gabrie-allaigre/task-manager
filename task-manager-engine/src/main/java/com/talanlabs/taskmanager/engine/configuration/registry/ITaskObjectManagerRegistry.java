package com.talanlabs.taskmanager.engine.configuration.registry;

import com.talanlabs.taskmanager.engine.manager.ITaskObjectManager;
import com.talanlabs.taskmanager.model.ITaskObject;

public interface ITaskObjectManagerRegistry {

    <E, F extends ITaskObject> ITaskObjectManager<E, F> getTaskObjectManager(F taskObject);

    <E, F extends ITaskObject> ITaskObjectManager<E, F> getTaskObjectManager(Class<F> taskObjectClass);

}
