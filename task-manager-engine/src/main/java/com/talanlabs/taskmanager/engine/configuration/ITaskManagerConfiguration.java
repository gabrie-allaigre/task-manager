package com.talanlabs.taskmanager.engine.configuration;

import com.talanlabs.taskmanager.engine.configuration.factory.ITaskFactory;
import com.talanlabs.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.talanlabs.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.talanlabs.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.talanlabs.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.talanlabs.taskmanager.engine.configuration.transform.ITaskChainCriteriaTransform;

public interface ITaskManagerConfiguration {

    ITaskObjectManagerRegistry getTaskObjectManagerRegistry();

    ITaskDefinitionRegistry getTaskDefinitionRegistry();

    ITaskFactory getTaskFactory();

    ITaskChainCriteriaTransform getTaskChainCriteriaBuilder();

    ITaskManagerReader getTaskManagerReader();

    ITaskManagerWriter getTaskManagerWriter();

}
