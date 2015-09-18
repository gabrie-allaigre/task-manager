package com.synaptix.taskmanager.example;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.Proxy;
import com.synaptix.taskmanager.engine.configuration.registry.DefaultTaskObjectManagerRegistry;
import com.synaptix.taskmanager.manager.ITaskObjectManager;
import com.synaptix.taskmanager.model.ITaskObject;

public class ComponentTaskObjectManagerRegistry extends DefaultTaskObjectManagerRegistry {

	@Override
	public <F extends ITaskObject<?>> ITaskObjectManager<F> getTaskObjectManager(F taskObject) {
		if (taskObject instanceof IComponent) {
			return getTaskObjectManager((Class<F>) ((Proxy) taskObject).straightGetComponentClass());
		}
		return super.getTaskObjectManager(taskObject);
	}
}
