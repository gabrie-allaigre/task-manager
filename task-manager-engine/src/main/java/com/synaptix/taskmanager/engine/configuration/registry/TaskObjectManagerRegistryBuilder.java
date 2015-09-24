package com.synaptix.taskmanager.engine.configuration.registry;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.model.ITaskObject;

public class TaskObjectManagerRegistryBuilder {

	private MyObjectManagerRegistry objectManagerRegistry;

	private TaskObjectManagerRegistryBuilder(IGetClass getClass) {
		super();

		this.objectManagerRegistry = new MyObjectManagerRegistry(getClass);
	}

	public TaskObjectManagerRegistryBuilder addTaskObjectManager(ITaskObjectManager<?> taskObjectManager) {
		objectManagerRegistry.taskObjectManagerMap.put(taskObjectManager.getTaskObjectClass(), taskObjectManager);
		return this;
	}

	public ITaskObjectManagerRegistry build() {
		return objectManagerRegistry;
	}

	public static TaskObjectManagerRegistryBuilder newBuilder() {
		return new TaskObjectManagerRegistryBuilder(null);
	}

	public static TaskObjectManagerRegistryBuilder newBuilder(IGetClass getClass) {
		return new TaskObjectManagerRegistryBuilder(getClass);
	}

	public interface IGetClass {

		public <F extends ITaskObject<?>> Class<F> getClass(F taskObject);

	}

	private static class MyObjectManagerRegistry extends AbstractTaskObjectManagerRegistry {

		private final IGetClass getClass;

		private final Map<Class<? extends ITaskObject<?>>, ITaskObjectManager<?>> taskObjectManagerMap;

		public MyObjectManagerRegistry(IGetClass getClass) {
			super();

			this.getClass = getClass;

			this.taskObjectManagerMap = new HashMap<Class<? extends ITaskObject<?>>, ITaskObjectManager<?>>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <F extends ITaskObject<?>> ITaskObjectManager<F> getTaskObjectManager(F taskObject) {
			if (getClass != null) {
				return getTaskObjectManager((Class<F>) getClass.getClass(taskObject));
			}
			return getTaskObjectManager((Class<F>) taskObject.getClass());
		}

		@SuppressWarnings("unchecked")
		@Override
		public <F extends ITaskObject<?>> ITaskObjectManager<F> getTaskObjectManager(Class<F> taskObjectClass) {
			return (ITaskObjectManager<F>) taskObjectManagerMap.get(taskObjectClass);
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
