package com.synaptix.taskmanager.engine.configuration.registry;

import com.synaptix.taskmanager.engine.taskdefinition.IStatusTaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.ISubTaskDefinition;

import java.util.HashMap;
import java.util.Map;

public class TaskDefinitionRegistryBuilder {

	private final MyTaskDefinitionRegistry taskDefinitionRegistry;

	private TaskDefinitionRegistryBuilder() {
		super();

		this.taskDefinitionRegistry = new MyTaskDefinitionRegistry();
	}

	public TaskDefinitionRegistryBuilder addStatusTaskDefinition(IStatusTaskDefinition taskDefinition) {
		taskDefinitionRegistry.statusTaskDefinitionMap.put(taskDefinition.getCode(), taskDefinition);
		return this;
	}

	public TaskDefinitionRegistryBuilder addSubTaskDefinition(ISubTaskDefinition taskDefinition) {
		taskDefinitionRegistry.subTaskDefinitionMap.put(taskDefinition.getCode(), taskDefinition);
		return this;
	}

	public ITaskDefinitionRegistry build() {
		return taskDefinitionRegistry;
	}

	public static TaskDefinitionRegistryBuilder newBuilder() {
		return new TaskDefinitionRegistryBuilder();
	}

	private static class MyTaskDefinitionRegistry extends AbstractTaskDefinitionRegistry {

		private Map<String, IStatusTaskDefinition> statusTaskDefinitionMap;

		private Map<String, ISubTaskDefinition> subTaskDefinitionMap;

		public MyTaskDefinitionRegistry() {
			super();

			this.statusTaskDefinitionMap = new HashMap<String, IStatusTaskDefinition>();
			this.subTaskDefinitionMap = new HashMap<String, ISubTaskDefinition>();
		}

		@Override
		public IStatusTaskDefinition getStatusTaskDefinition(String code) {
			return statusTaskDefinitionMap.get(code);
		}

		@Override
		public ISubTaskDefinition getSubTaskDefinition(String code) {
			return subTaskDefinitionMap.get(code);
		}
	}
}
