package com.synaptix.taskmanager.engine.configuration.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.synaptix.taskmanager.manager.taskdefinition.INormalTaskDefinition;
import com.synaptix.taskmanager.manager.taskdefinition.IUpdateStatusTaskDefinition;

public class DefaultTaskDefinitionRegistry extends AbstractTaskDefinitionRegistry {

	private Map<String, IUpdateStatusTaskDefinition> updateStatusTaskDefinitionMap;

	private Map<String, INormalTaskDefinition> normalTaskDefinitionMap;

	public DefaultTaskDefinitionRegistry() {
		super();

		this.updateStatusTaskDefinitionMap = new HashMap<String, IUpdateStatusTaskDefinition>();
		this.normalTaskDefinitionMap = new HashMap<String, INormalTaskDefinition>();
	}

	public void addUpdateStatusTaskDefinition(IUpdateStatusTaskDefinition taskDefinition) {
		updateStatusTaskDefinitionMap.put(taskDefinition.getCode(), taskDefinition);
	}

	public void removeUpdateStatusTaskDefinition(IUpdateStatusTaskDefinition taskDefinition) {
		updateStatusTaskDefinitionMap.remove(taskDefinition.getCode());
	}

	public Collection<IUpdateStatusTaskDefinition> getUpdateStatusTaskDefinitions() {
		return Collections.unmodifiableCollection(updateStatusTaskDefinitionMap.values());
	}

	@Override
	public IUpdateStatusTaskDefinition getUpdateStatusTaskDefinition(String code) {
		return updateStatusTaskDefinitionMap.get(code);
	}

	public void addNormalTaskDefinition(INormalTaskDefinition taskDefinition) {
		normalTaskDefinitionMap.put(taskDefinition.getCode(), taskDefinition);
	}

	public void removeNormalTaskDefinition(INormalTaskDefinition taskDefinition) {
		normalTaskDefinitionMap.remove(taskDefinition.getCode());
	}

	public Collection<INormalTaskDefinition> getNormalTaskDefinitions() {
		return Collections.unmodifiableCollection(normalTaskDefinitionMap.values());
	}

	@Override
	public INormalTaskDefinition getNormalTaskDefinition(String code) {
		return normalTaskDefinitionMap.get(code);
	}
}
