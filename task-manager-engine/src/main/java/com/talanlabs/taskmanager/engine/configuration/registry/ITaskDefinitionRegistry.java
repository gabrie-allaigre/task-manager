package com.talanlabs.taskmanager.engine.configuration.registry;

import com.talanlabs.taskmanager.engine.taskdefinition.ITaskDefinition;

public interface ITaskDefinitionRegistry {

    /**
     * Return TaskDefinition with code
     *
     * @param code code
     * @return task definition
     */
    ITaskDefinition getTaskDefinition(String code);

}
