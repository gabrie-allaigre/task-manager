package com.synaptix.taskmanager.engine.configuration.registry;

import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;

import java.util.HashMap;
import java.util.Map;

public class TaskDefinitionRegistryBuilder {

	private final MyTaskDefinitionRegistry taskDefinitionRegistry;

	private TaskDefinitionRegistryBuilder() {
		super();

		this.taskDefinitionRegistry = new MyTaskDefinitionRegistry();
	}

	public TaskDefinitionRegistryBuilder addTaskDefinition(ITaskDefinition taskDefinition) {
		taskDefinitionRegistry.taskDefinitionMap.put(taskDefinition.getCode(), taskDefinition);
		return this;
	}

	public ITaskDefinitionRegistry build() {
		return taskDefinitionRegistry;
	}

	public static TaskDefinitionRegistryBuilder newBuilder() {
		return new TaskDefinitionRegistryBuilder();
	}

	private static class MyTaskDefinitionRegistry extends AbstractTaskDefinitionRegistry {

		private Map<String, ITaskDefinition> taskDefinitionMap;

		public MyTaskDefinitionRegistry() {
			super();

			this.taskDefinitionMap = new HashMap<String, ITaskDefinition>();
		}

		@Override
		public ITaskDefinition getTaskDefinition(String code) {
			return taskDefinitionMap.get(code);
		}

	}
}
