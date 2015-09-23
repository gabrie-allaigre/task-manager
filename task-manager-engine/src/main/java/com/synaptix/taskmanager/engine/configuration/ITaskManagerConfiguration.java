package com.synaptix.taskmanager.engine.configuration;

import com.synaptix.taskmanager.engine.configuration.factory.ITaskFactory;
import com.synaptix.taskmanager.engine.configuration.graph.IStatusGraphRegistry;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.transform.ITaskChainCriteriaTransform;

public interface ITaskManagerConfiguration {

	public IStatusGraphRegistry getStatusGraphsRegistry();

	public ITaskObjectManagerRegistry getTaskObjectManagerRegistry();

	public ITaskDefinitionRegistry getTaskDefinitionRegistry();

	public ITaskFactory getTaskFactory();

	public ITaskChainCriteriaTransform getTaskChainCriteriaBuilder();

	public ITaskManagerReader getTaskManagerReader();

	public ITaskManagerWriter getTaskManagerWriter();

}
