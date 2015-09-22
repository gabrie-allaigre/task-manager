package com.synaptix.taskmanager.engine.configuration.registry;

import com.synaptix.taskmanager.manager.taskdefinition.INormalTaskDefinition;
import com.synaptix.taskmanager.manager.taskdefinition.IUpdateStatusTaskDefinition;

public interface ITaskDefinitionRegistry {

	/**
	 * Return TaskDefinition for update status
	 * 
	 * @param code
	 * @return
	 */
	public IUpdateStatusTaskDefinition getUpdateStatusTaskDefinition(String code);

	/**
	 * Return TaskDefinition with service code
	 * 
	 * @param code
	 * @return
	 */
	public INormalTaskDefinition getNormalTaskDefinition(String code);

}
