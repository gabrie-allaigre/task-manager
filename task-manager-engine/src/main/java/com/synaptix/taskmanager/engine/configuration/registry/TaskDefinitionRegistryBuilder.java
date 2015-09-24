package com.synaptix.taskmanager.engine.configuration.registry;

import java.util.HashMap;
import java.util.Map;

import com.synaptix.taskmanager.engine.taskdefinition.INormalTaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.IUpdateStatusTaskDefinition;

public class TaskDefinitionRegistryBuilder {

	private final MyTaskDefinitionRegistry taskDefinitionRegistry;

	private TaskDefinitionRegistryBuilder() {
		super();

		this.taskDefinitionRegistry = new MyTaskDefinitionRegistry();
	}

	public TaskDefinitionRegistryBuilder addUpdateStatusTaskDefinition(IUpdateStatusTaskDefinition taskDefinition) {
		taskDefinitionRegistry.updateStatusTaskDefinitionMap.put(taskDefinition.getCode(), taskDefinition);
		return this;
	}

	public TaskDefinitionRegistryBuilder addNormalTaskDefinition(INormalTaskDefinition taskDefinition) {
		taskDefinitionRegistry.normalTaskDefinitionMap.put(taskDefinition.getCode(), taskDefinition);
		return this;
	}

	public ITaskDefinitionRegistry build() {
		return taskDefinitionRegistry;
	}

	public static TaskDefinitionRegistryBuilder newBuilder() {
		return new TaskDefinitionRegistryBuilder();
	}

	private static class MyTaskDefinitionRegistry extends AbstractTaskDefinitionRegistry {

		private Map<String, IUpdateStatusTaskDefinition> updateStatusTaskDefinitionMap;

		private Map<String, INormalTaskDefinition> normalTaskDefinitionMap;

		public MyTaskDefinitionRegistry() {
			super();

			this.updateStatusTaskDefinitionMap = new HashMap<String, IUpdateStatusTaskDefinition>();
			this.normalTaskDefinitionMap = new HashMap<String, INormalTaskDefinition>();
		}

		@Override
		public IUpdateStatusTaskDefinition getUpdateStatusTaskDefinition(String code) {
			return updateStatusTaskDefinitionMap.get(code);
		}

		@Override
		public INormalTaskDefinition getNormalTaskDefinition(String code) {
			return normalTaskDefinitionMap.get(code);
		}
	}
}
