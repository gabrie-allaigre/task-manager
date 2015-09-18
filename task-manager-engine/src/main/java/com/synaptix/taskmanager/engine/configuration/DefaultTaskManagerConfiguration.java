package com.synaptix.taskmanager.engine.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.taskmanager.engine.ITaskManagerReader;
import com.synaptix.taskmanager.engine.ITaskManagerWriter;
import com.synaptix.taskmanager.engine.MemoryTaskManagerReaderWriter;
import com.synaptix.taskmanager.engine.configuration.factory.ITaskFactory;
import com.synaptix.taskmanager.engine.configuration.registry.DefaultTaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.DefaultTaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.result.DefaultTaskResultDetailBuilder;
import com.synaptix.taskmanager.engine.configuration.result.ITaskResultDetailBuilder;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.simple.SimpleTaskFactory;

public class DefaultTaskManagerConfiguration extends AbstractTaskManagerConfiguration {

	private Map<Class<? extends ITaskObject<?>>, List<? extends IStatusGraph<?>>> statusGraphMap;

	private ITaskObjectManagerRegistry taskObjectManagerRegistry;

	private ITaskDefinitionRegistry taskDefinitionRegistry;

	private ITaskFactory taskFactory;

	private ITaskResultDetailBuilder taskResultDetailBuilder;

	private ITaskManagerReader taskManagerReader;

	private ITaskManagerWriter taskManagerWriter;

	public DefaultTaskManagerConfiguration() {
		super();

		this.statusGraphMap = new HashMap<Class<? extends ITaskObject<?>>, List<? extends IStatusGraph<?>>>();

		this.taskObjectManagerRegistry = new DefaultTaskObjectManagerRegistry();
		this.taskDefinitionRegistry = new DefaultTaskDefinitionRegistry();
		this.taskFactory = new SimpleTaskFactory();
		this.taskResultDetailBuilder = new DefaultTaskResultDetailBuilder();

		MemoryTaskManagerReaderWriter memoryTaskReaderWriter = new MemoryTaskManagerReaderWriter();
		this.taskManagerReader = memoryTaskReaderWriter;
		this.taskManagerWriter = memoryTaskReaderWriter;

	}

	public <E extends Enum<E>, F extends ITaskObject<E>> void addStatusGraphs(Class<F> taskObjectClass, List<IStatusGraph<E>> statusGraphs) {
		this.statusGraphMap.put(taskObjectClass, statusGraphs);
	}

	public <F extends ITaskObject<?>> void removeStatusGraphs(Class<F> taskObjectClass) {
		this.statusGraphMap.remove(taskObjectClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends Enum<E>, F extends ITaskObject<E>> List<IStatusGraph<E>> getStatusGraphsByTaskObjectType(Class<F> taskObjectClass) {
		return (List<IStatusGraph<E>>) statusGraphMap.get(taskObjectClass);
	}

	public void setTaskObjectManagerRegistry(ITaskObjectManagerRegistry taskObjectManagerRegistry) {
		this.taskObjectManagerRegistry = taskObjectManagerRegistry;
	}

	@Override
	public ITaskObjectManagerRegistry getTaskObjectManagerRegistry() {
		return this.taskObjectManagerRegistry;
	}

	public void setTaskDefinitionRegistry(ITaskDefinitionRegistry taskServiceRegistry) {
		this.taskDefinitionRegistry = taskServiceRegistry;
	}

	@Override
	public ITaskDefinitionRegistry getTaskDefinitionRegistry() {
		return this.taskDefinitionRegistry;
	}

	public void setTaskFactory(ITaskFactory taskFactory) {
		this.taskFactory = taskFactory;
	}

	@Override
	public ITaskFactory getTaskFactory() {
		return this.taskFactory;
	}

	public void setTaskResultDetailBuilder(ITaskResultDetailBuilder resultDetailBuilder) {
		this.taskResultDetailBuilder = resultDetailBuilder;
	}

	@Override
	public ITaskResultDetailBuilder getTaskResultDetailBuilder() {
		return this.taskResultDetailBuilder;
	}

	public void setTaskManagerReader(ITaskManagerReader taskManagerReader) {
		this.taskManagerReader = taskManagerReader;
	}

	@Override
	public ITaskManagerReader getTaskManagerReader() {
		return taskManagerReader;
	}

	public void setTaskManagerWriter(ITaskManagerWriter taskManagerWriter) {
		this.taskManagerWriter = taskManagerWriter;
	}

	@Override
	public ITaskManagerWriter getTaskManagerWriter() {
		return taskManagerWriter;
	}

	public static class Builder {

		private DefaultTaskManagerConfiguration taskManagerConfiguration;

		public Builder() {
			super();

			this.taskManagerConfiguration = new DefaultTaskManagerConfiguration();
		}

		public <E extends Enum<E>, F extends ITaskObject<E>> Builder addStatusGraphs(Class<F> taskObjectClass, List<IStatusGraph<E>> statusGraphs) {
			this.taskManagerConfiguration.addStatusGraphs(taskObjectClass, statusGraphs);
			return this;
		}

		public Builder taskObjectManagerRegistry(ITaskObjectManagerRegistry taskObjectManagerRegistry) {
			this.taskManagerConfiguration.setTaskObjectManagerRegistry(taskObjectManagerRegistry);
			return this;
		}

		public Builder taskServiceRegistry(ITaskDefinitionRegistry taskServiceRegistry) {
			this.taskManagerConfiguration.setTaskDefinitionRegistry(taskServiceRegistry);
			return this;
		}

		public Builder taskFactory(ITaskFactory taskFactory) {
			this.taskManagerConfiguration.setTaskFactory(taskFactory);
			return this;
		}

		public Builder resultDetailBuilder(ITaskResultDetailBuilder resultDetailBuilder) {
			this.taskManagerConfiguration.setTaskResultDetailBuilder(resultDetailBuilder);
			return this;
		}

		public Builder taskManagerReader(ITaskManagerReader taskManagerReader) {
			this.taskManagerConfiguration.setTaskManagerReader(taskManagerReader);
			return this;
		}

		public Builder taskManagerWriter(ITaskManagerWriter taskManagerWriter) {
			this.taskManagerConfiguration.setTaskManagerWriter(taskManagerWriter);
			return this;
		}

		public DefaultTaskManagerConfiguration build() {
			return this.taskManagerConfiguration;
		}
	}

	public static class StatusGraphsBuilder<E extends Enum<E>> {

		private final E currentStatus;

		private final String codeTaskType;

		private List<IStatusGraph<E>> statusGraphs = new ArrayList<IStatusGraph<E>>();

		private List<StatusGraphsBuilder<E>> statusGraphsBuilders = new ArrayList<StatusGraphsBuilder<E>>();

		public StatusGraphsBuilder(E currentStatus, String codeTaskType) {
			super();

			this.currentStatus = currentStatus;
			this.codeTaskType = codeTaskType;
		}

		public StatusGraphsBuilder<E> addNextStatusGraph(final E nextStatus, final String codeTaskType) {
			statusGraphs.add(new DefaultStatusGraph(currentStatus, nextStatus, codeTaskType));
			return this;
		}

		public StatusGraphsBuilder<E> addNextStatusGraphsBuilder(StatusGraphsBuilder<E> statusGraphsBuilder) {
			statusGraphsBuilders.add(statusGraphsBuilder);
			return this;
		}

		public List<IStatusGraph<E>> build() {
			List<IStatusGraph<E>> res = new ArrayList<IStatusGraph<E>>();
			res.add(new DefaultStatusGraph(null, currentStatus, codeTaskType));
			res.addAll(_build());
			return res;
		}

		private List<IStatusGraph<E>> _build() {
			List<IStatusGraph<E>> res = new ArrayList<IStatusGraph<E>>();
			res.addAll(statusGraphs);
			for (StatusGraphsBuilder<E> statusGraphsBuilder : statusGraphsBuilders) {
				res.add(new DefaultStatusGraph(currentStatus, statusGraphsBuilder.currentStatus, statusGraphsBuilder.codeTaskType));
				res.addAll(statusGraphsBuilder._build());
			}
			return res;
		}

		public static <E extends Enum<E>> StatusGraphsBuilder<E> newBuilder(E currentStatus, String codeTaskType) {
			return new StatusGraphsBuilder<E>(currentStatus, codeTaskType);
		}

		private class DefaultStatusGraph implements IStatusGraph<E> {

			private E currentStatus;

			private E nextStatus;

			private String codeTaskType;

			public DefaultStatusGraph(E currentStatus, E nextStatus, String codeTaskType) {
				super();

				this.currentStatus = currentStatus;
				this.nextStatus = nextStatus;
				this.codeTaskType = codeTaskType;
			}

			@Override
			public E getCurrentStatus() {
				return currentStatus;
			}

			@Override
			public E getNextStatus() {
				return nextStatus;
			}

			@Override
			public String getCodeTaskType() {
				return codeTaskType;
			}
		}
	}
}
