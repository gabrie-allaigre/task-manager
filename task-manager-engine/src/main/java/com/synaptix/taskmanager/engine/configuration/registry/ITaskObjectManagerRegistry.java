package com.synaptix.taskmanager.engine.configuration.registry;

import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskObjectManagerRegistry {

	<E,F extends ITaskObject> ITaskObjectManager<E,F> getTaskObjectManager(F taskObject);

	<E,F extends ITaskObject> ITaskObjectManager<E,F> getTaskObjectManager(Class<F> taskObjectClass);

}
