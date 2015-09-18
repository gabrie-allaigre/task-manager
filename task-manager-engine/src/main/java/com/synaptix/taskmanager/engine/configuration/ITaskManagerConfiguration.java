package com.synaptix.taskmanager.engine.configuration;

import java.util.List;

import com.synaptix.taskmanager.engine.ITaskManagerReader;
import com.synaptix.taskmanager.engine.ITaskManagerWriter;
import com.synaptix.taskmanager.engine.configuration.factory.ITaskFactory;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.result.ITaskResultDetailBuilder;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskManagerConfiguration {

	/**
	 * Find status graph by Task Object Type
	 * 
	 * @param taskObjectClass
	 * @return
	 */
	public <E extends Enum<E>, F extends ITaskObject<E>> List<IStatusGraph<E>> getStatusGraphsByTaskObjectType(Class<F> taskObjectClass);

	public ITaskObjectManagerRegistry getTaskObjectManagerRegistry();

	public ITaskDefinitionRegistry getTaskDefinitionRegistry();

	public ITaskFactory getTaskFactory();

	public ITaskResultDetailBuilder getTaskResultDetailBuilder();

	public ITaskManagerReader getTaskManagerReader();

	public ITaskManagerWriter getTaskManagerWriter();

}
