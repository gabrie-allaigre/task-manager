package com.synaptix.taskmanager.engine.configuration.registry;

import com.synaptix.taskmanager.manager.ITaskObjectManager;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskObjectManagerRegistry {

	public <F extends ITaskObject<?>> ITaskObjectManager<F> getTaskObjectManager(F taskObject);

	public <F extends ITaskObject<?>> ITaskObjectManager<F> getTaskObjectManager(Class<F> taskObjectClass);

}
