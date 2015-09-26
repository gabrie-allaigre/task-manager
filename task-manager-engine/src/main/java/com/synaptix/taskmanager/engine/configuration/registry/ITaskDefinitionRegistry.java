package com.synaptix.taskmanager.engine.configuration.registry;

import com.synaptix.taskmanager.engine.taskdefinition.INormalTaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.IUpdateStatusTaskDefinition;

public interface ITaskDefinitionRegistry {

	/**
	 * Return TaskDefinition for update status
	 * 
	 * @param code
	 * @return
	 */
	IUpdateStatusTaskDefinition getUpdateStatusTaskDefinition(String code);

	/**
	 * Return TaskDefinition with service code
	 * 
	 * @param code
	 * @return
	 */
	INormalTaskDefinition getNormalTaskDefinition(String code);

}
