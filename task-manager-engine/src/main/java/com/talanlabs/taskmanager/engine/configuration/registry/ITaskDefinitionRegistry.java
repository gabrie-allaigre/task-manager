package com.talanlabs.taskmanager.engine.configuration.registry;

import com.talanlabs.taskmanager.engine.taskdefinition.ITaskDefinition;

public interface ITaskDefinitionRegistry {

    /**
     * Return TaskDefinition
     *
     * @param code
     * @return
     */
    ITaskDefinition getTaskDefinition(String code);

}
