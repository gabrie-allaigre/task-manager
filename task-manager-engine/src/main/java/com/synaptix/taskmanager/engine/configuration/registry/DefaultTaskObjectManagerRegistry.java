package com.synaptix.taskmanager.engine.configuration.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.synaptix.taskmanager.manager.ITaskObjectManager;
import com.synaptix.taskmanager.model.ITaskObject;

public class DefaultTaskObjectManagerRegistry extends AbstractTaskObjectManagerRegistry {

	private Map<Class<? extends ITaskObject<?>>, ITaskObjectManager<?>> taskObjectManagerMap;

	public DefaultTaskObjectManagerRegistry() {
		super();

		taskObjectManagerMap = new HashMap<Class<? extends ITaskObject<?>>, ITaskObjectManager<?>>();
	}

	public void addTaskObjectManager(ITaskObjectManager<?> taskObjectManager) {
		taskObjectManagerMap.put(taskObjectManager.getTaskObjectClass(), taskObjectManager);
	}

	public void removeTaskObjectManager(ITaskObjectManager<?> taskObjectManager) {
		taskObjectManagerMap.remove(taskObjectManager.getTaskObjectClass());
	}

	public Collection<ITaskObjectManager<?>> getTaskObjectManagers() {
		return Collections.unmodifiableCollection(taskObjectManagerMap.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F extends ITaskObject<?>> ITaskObjectManager<F> getTaskObjectManager(F taskObject) {
		return getTaskObjectManager((Class<F>) taskObject.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F extends ITaskObject<?>> ITaskObjectManager<F> getTaskObjectManager(Class<F> taskObjectClass) {
		return (ITaskObjectManager<F>) taskObjectManagerMap.get(taskObjectClass);
	}
}
