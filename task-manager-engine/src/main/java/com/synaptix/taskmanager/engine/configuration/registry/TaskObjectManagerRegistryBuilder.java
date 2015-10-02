package com.synaptix.taskmanager.engine.configuration.registry;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.model.ITaskObject;

public class TaskObjectManagerRegistryBuilder {

	private MyObjectManagerRegistry objectManagerRegistry;

	private TaskObjectManagerRegistryBuilder() {
		super();

		this.objectManagerRegistry = new MyObjectManagerRegistry();
	}

	public TaskObjectManagerRegistryBuilder instanceToClass(IInstanceToClass instanceToClass) {
		objectManagerRegistry.instanceToClass = instanceToClass;
		return this;
	}

	public TaskObjectManagerRegistryBuilder addTaskObjectManager(ITaskObjectManager<?,?> taskObjectManager) {
		objectManagerRegistry.taskObjectManagerMap.put(taskObjectManager.getTaskObjectClass(), taskObjectManager);
		return this;
	}

	public ITaskObjectManagerRegistry build() {
		return objectManagerRegistry;
	}

	public static TaskObjectManagerRegistryBuilder newBuilder() {
		return new TaskObjectManagerRegistryBuilder();
	}

	public interface IInstanceToClass {

		<F extends ITaskObject> Class<F> instanceToClass(F taskObject);

	}

	private static class MyObjectManagerRegistry extends AbstractTaskObjectManagerRegistry {

		private final Map<Class<? extends ITaskObject>, ITaskObjectManager<?,?>> taskObjectManagerMap;

		private IInstanceToClass instanceToClass;

		public MyObjectManagerRegistry() {
			super();

			this.taskObjectManagerMap = new HashMap<>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <E,F extends ITaskObject> ITaskObjectManager<E,F> getTaskObjectManager(F taskObject) {
			if (instanceToClass != null) {
				return getTaskObjectManager(instanceToClass.instanceToClass(taskObject));
			}
			return getTaskObjectManager((Class<F>) taskObject.getClass());
		}

		@SuppressWarnings("unchecked")
		@Override
		public <E,F extends ITaskObject> ITaskObjectManager<E,F> getTaskObjectManager(Class<F> taskObjectClass) {
			return (ITaskObjectManager<E,F>) taskObjectManagerMap.get(taskObjectClass);
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
