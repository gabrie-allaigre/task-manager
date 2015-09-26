package com.synaptix.taskmanager.engine.configuration.registry;

import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskObjectManagerRegistry {

	<E extends Object,F extends ITaskObject<E>> ITaskObjectManager<E,F> getTaskObjectManager(F taskObject);

	<E extends Object,F extends ITaskObject<E>> ITaskObjectManager<E,F> getTaskObjectManager(Class<F> taskObjectClass);

}
