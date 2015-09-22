package com.synaptix.taskmanager.engine.configuration;

import com.synaptix.taskmanager.engine.configuration.factory.ITaskFactory;
import com.synaptix.taskmanager.engine.configuration.graph.DefaultStatusGraphRegistry;
import com.synaptix.taskmanager.engine.configuration.graph.IStatusGraphRegistry;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.synaptix.taskmanager.engine.configuration.registry.DefaultTaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.DefaultTaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.simple.MemoryTaskManagerReaderWriter;
import com.synaptix.taskmanager.simple.SimpleTaskFactory;

public class TaskManagerConfigurationBuilder {

	private TaskManagerConfigurationImpl taskManagerConfiguration;

	protected TaskManagerConfigurationBuilder() {
		super();

		this.taskManagerConfiguration = new TaskManagerConfigurationImpl();
	}

	public <E extends Enum<E>, F extends ITaskObject<E>> TaskManagerConfigurationBuilder statusGraphRegistry(IStatusGraphRegistry statusGraphRegistry) {
		this.taskManagerConfiguration.statusGraphRegistry = statusGraphRegistry;
		return this;
	}

	public TaskManagerConfigurationBuilder taskObjectManagerRegistry(ITaskObjectManagerRegistry taskObjectManagerRegistry) {
		this.taskManagerConfiguration.taskObjectManagerRegistry = taskObjectManagerRegistry;
		return this;
	}

	public TaskManagerConfigurationBuilder taskServiceRegistry(ITaskDefinitionRegistry taskDefinitionRegistry) {
		this.taskManagerConfiguration.taskDefinitionRegistry = taskDefinitionRegistry;
		return this;
	}

	public TaskManagerConfigurationBuilder taskFactory(ITaskFactory taskFactory) {
		this.taskManagerConfiguration.taskFactory = taskFactory;
		return this;
	}

	public TaskManagerConfigurationBuilder taskManagerReader(ITaskManagerReader taskManagerReader) {
		this.taskManagerConfiguration.taskManagerReader = taskManagerReader;
		return this;
	}

	public TaskManagerConfigurationBuilder taskManagerWriter(ITaskManagerWriter taskManagerWriter) {
		this.taskManagerConfiguration.taskManagerWriter = taskManagerWriter;
		return this;
	}

	public ITaskManagerConfiguration build() {
		return this.taskManagerConfiguration;
	}

	public static TaskManagerConfigurationBuilder newBuilder() {
		return new TaskManagerConfigurationBuilder();
	}

	private static class TaskManagerConfigurationImpl implements ITaskManagerConfiguration {

		private IStatusGraphRegistry statusGraphRegistry;

		private ITaskObjectManagerRegistry taskObjectManagerRegistry;

		private ITaskDefinitionRegistry taskDefinitionRegistry;

		private ITaskFactory taskFactory;

		private ITaskManagerReader taskManagerReader;

		private ITaskManagerWriter taskManagerWriter;

		public TaskManagerConfigurationImpl() {
			super();

			this.statusGraphRegistry = new DefaultStatusGraphRegistry();
			this.taskObjectManagerRegistry = new DefaultTaskObjectManagerRegistry();
			this.taskDefinitionRegistry = new DefaultTaskDefinitionRegistry();
			this.taskFactory = new SimpleTaskFactory();

			MemoryTaskManagerReaderWriter memoryTaskReaderWriter = new MemoryTaskManagerReaderWriter();
			this.taskManagerReader = memoryTaskReaderWriter;
			this.taskManagerWriter = memoryTaskReaderWriter;

		}

		@Override
		public IStatusGraphRegistry getStatusGraphsRegistry() {
			return this.statusGraphRegistry;
		}

		@Override
		public ITaskObjectManagerRegistry getTaskObjectManagerRegistry() {
			return this.taskObjectManagerRegistry;
		}

		@Override
		public ITaskDefinitionRegistry getTaskDefinitionRegistry() {
			return this.taskDefinitionRegistry;
		}

		@Override
		public ITaskFactory getTaskFactory() {
			return this.taskFactory;
		}

		@Override
		public ITaskManagerReader getTaskManagerReader() {
			return taskManagerReader;
		}

		@Override
		public ITaskManagerWriter getTaskManagerWriter() {
			return taskManagerWriter;
		}
	}
}
