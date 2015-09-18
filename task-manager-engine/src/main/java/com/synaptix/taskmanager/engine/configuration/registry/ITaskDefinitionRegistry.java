package com.synaptix.taskmanager.engine.configuration.registry;

import com.synaptix.taskmanager.manager.taskdefinition.ITaskDefinition;

public interface ITaskDefinitionRegistry {

	/**
	 * Return TaskDefinition with service code
	 * 
	 * @param code
	 * @return
	 */
	public ITaskDefinition getTaskDefinition(String code);

}
