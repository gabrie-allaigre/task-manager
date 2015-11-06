package com.synaptix.taskmanager.engine.taskdefinition;

import com.synaptix.taskmanager.engine.taskservice.ITaskService;

public class TaskDefinitionBuilder {

    private DefaultTaskDefinition taskDefinition;

    private TaskDefinitionBuilder(String code, ITaskService taskService) {
        super();

        this.taskDefinition = new DefaultTaskDefinition(code, taskService);
    }

    public static TaskDefinitionBuilder newBuilder(String code, ITaskService taskService) {
        return new TaskDefinitionBuilder(code, taskService);
    }

    public ITaskDefinition build() {
        return taskDefinition;
    }
}
