package com.synaptix.taskmanager.engine.taskdefinition;

import com.synaptix.taskmanager.engine.taskservice.ITaskService;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DefaultTaskDefinition implements ITaskDefinition {

    private final String code;

    private final ITaskService taskService;

    public DefaultTaskDefinition(String code, ITaskService taskService) {
        super();

        this.code = code;
        this.taskService = taskService;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public ITaskService getTaskService() {
        return this.taskService;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
