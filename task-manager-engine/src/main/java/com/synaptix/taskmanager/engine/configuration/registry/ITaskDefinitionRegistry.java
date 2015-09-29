package com.synaptix.taskmanager.engine.configuration.registry;

import com.synaptix.taskmanager.engine.taskdefinition.ISubTaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.IStatusTaskDefinition;

public interface ITaskDefinitionRegistry {

	/**
	 * Return TaskDefinition for update status
	 * 
	 * @param code
	 * @return
	 */
	IStatusTaskDefinition getStatusTaskDefinition(String code);

	/**
	 * Return TaskDefinition with service code
	 * 
	 * @param code
	 * @return
	 */
	ISubTaskDefinition getSubTaskDefinition(String code);

}
