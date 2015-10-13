package com.synaptix.taskmanager.engine.configuration.registry;

import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;

public interface ITaskDefinitionRegistry {

    /**
     * Return TaskDefinition
     *
     * @param code
     * @return
     */
    ITaskDefinition getTaskDefinition(String code);

}
