package com.synaptix.taskmanager.engine.taskdefinition;

import com.synaptix.taskmanager.engine.taskservice.ITaskService;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TaskDefinitionBuilder {

    private MyTaskDefinition taskDefinition;

    private TaskDefinitionBuilder(String code, ITaskService taskService) {
        super();

        this.taskDefinition = new MyTaskDefinition(code, taskService);
    }

    public static TaskDefinitionBuilder newBuilder(String code, ITaskService taskService) {
        return new TaskDefinitionBuilder(code, taskService);
    }

    public ITaskDefinition build() {
        return taskDefinition;
    }

    private static class MyTaskDefinition implements ITaskDefinition {

        private final String code;

        private final ITaskService taskService;

        public MyTaskDefinition(String code, ITaskService taskService) {
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
}
