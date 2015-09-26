package com.synaptix.taskmanager.engine.configuration;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.synaptix.taskmanager.engine.configuration.factory.ITaskFactory;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.transform.DefaultTaskChainCriteriaTransform;
import com.synaptix.taskmanager.engine.configuration.transform.ITaskChainCriteriaTransform;
import com.synaptix.taskmanager.engine.memory.MemoryTaskManagerReaderWriter;
import com.synaptix.taskmanager.engine.memory.SimpleTaskFactory;
import com.synaptix.taskmanager.model.ITaskObject;

public class TaskManagerConfigurationBuilder {

	private TaskManagerConfigurationImpl taskManagerConfiguration;

	private TaskManagerConfigurationBuilder() {
		super();

		this.taskManagerConfiguration = new TaskManagerConfigurationImpl();
	}

	public TaskManagerConfigurationBuilder taskObjectManagerRegistry(ITaskObjectManagerRegistry taskObjectManagerRegistry) {
		this.taskManagerConfiguration.taskObjectManagerRegistry = taskObjectManagerRegistry;
		return this;
	}

	public TaskManagerConfigurationBuilder taskDefinitionRegistry(ITaskDefinitionRegistry taskDefinitionRegistry) {
		this.taskManagerConfiguration.taskDefinitionRegistry = taskDefinitionRegistry;
		return this;
	}

	public TaskManagerConfigurationBuilder taskFactory(ITaskFactory taskFactory) {
		this.taskManagerConfiguration.taskFactory = taskFactory;
		return this;
	}

	public TaskManagerConfigurationBuilder taskChainCriteriaBuilder(ITaskChainCriteriaTransform taskChainCriteriaBuilder) {
		this.taskManagerConfiguration.taskChainCriteriaBuilder = taskChainCriteriaBuilder;
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

		private ITaskObjectManagerRegistry taskObjectManagerRegistry;

		private ITaskDefinitionRegistry taskDefinitionRegistry;

		private ITaskFactory taskFactory;

		private ITaskChainCriteriaTransform taskChainCriteriaBuilder;

		private ITaskManagerReader taskManagerReader;

		private ITaskManagerWriter taskManagerWriter;

		public TaskManagerConfigurationImpl() {
			super();

			this.taskFactory = new SimpleTaskFactory();
			this.taskChainCriteriaBuilder = new DefaultTaskChainCriteriaTransform();

			MemoryTaskManagerReaderWriter memoryTaskReaderWriter = new MemoryTaskManagerReaderWriter();
			this.taskManagerReader = memoryTaskReaderWriter;
			this.taskManagerWriter = memoryTaskReaderWriter;

		}

		@Override
		public ITaskObjectManagerRegistry getTaskObjectManagerRegistry() {
			return taskObjectManagerRegistry;
		}

		@Override
		public ITaskDefinitionRegistry getTaskDefinitionRegistry() {
			return taskDefinitionRegistry;
		}

		@Override
		public ITaskFactory getTaskFactory() {
			return taskFactory;
		}

		@Override
		public ITaskChainCriteriaTransform getTaskChainCriteriaBuilder() {
			return taskChainCriteriaBuilder;
		}

		@Override
		public ITaskManagerReader getTaskManagerReader() {
			return taskManagerReader;
		}

		@Override
		public ITaskManagerWriter getTaskManagerWriter() {
			return taskManagerWriter;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
