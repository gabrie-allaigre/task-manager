package com.synaptix.taskmanager.engine.configuration.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.synaptix.taskmanager.manager.taskdefinition.ITaskDefinition;

public class DefaultTaskDefinitionRegistry extends AbstractTaskDefinitionRegistry {

	private Map<String, ITaskDefinition> taskDefinitionMap;

	public DefaultTaskDefinitionRegistry() {
		super();

		this.taskDefinitionMap = new HashMap<String, ITaskDefinition>();
	}

	public void addTaskDefinition(ITaskDefinition taskDefinition) {
		taskDefinitionMap.put(taskDefinition.getCode(), taskDefinition);
	}

	public void removeTaskDefinition(ITaskDefinition taskDefinition) {
		taskDefinitionMap.remove(taskDefinition.getCode());
	}

	public Collection<ITaskDefinition> getTaskDefinitions() {
		return Collections.unmodifiableCollection(taskDefinitionMap.values());
	}

	@Override
	public ITaskDefinition getTaskDefinition(String code) {
		return taskDefinitionMap.get(code);
	}
}
