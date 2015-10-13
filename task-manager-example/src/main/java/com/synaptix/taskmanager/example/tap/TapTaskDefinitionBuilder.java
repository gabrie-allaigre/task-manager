package com.synaptix.taskmanager.example.tap;

import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.synaptix.taskmanager.engine.taskservice.ITaskService;
import com.synaptix.taskmanager.example.tap.model.FicheContactStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TapTaskDefinitionBuilder {

    private MyTapTaskDefinition taskDefinition;

    private TapTaskDefinitionBuilder(String code, ITaskService taskService) {
        super();

        this.taskDefinition = new MyTapTaskDefinition(code, taskService);
    }

    public TapTaskDefinitionBuilder type(String type) {
        taskDefinition.type = type;
        return this;
    }

    public TapTaskDefinitionBuilder endFicheContactStatus(FicheContactStatus endFicheContactStatus) {
        taskDefinition.endFicheContactStatus = endFicheContactStatus;
        return this;
    }

    public static TapTaskDefinitionBuilder newBuilder(String code, ITaskService taskService) {
        return new TapTaskDefinitionBuilder(code, taskService);
    }

    public ITaskDefinition build() {
        return taskDefinition;
    }

    private static class MyTapTaskDefinition implements ITapTaskDefinition {

        private final String code;

        private final ITaskService taskService;

        private String type;

        private FicheContactStatus endFicheContactStatus;

        public MyTapTaskDefinition(String code, ITaskService taskService) {
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

        @Override
        public String getType() {
            return type;
        }

        @Override
        public FicheContactStatus getEndFicheContactStatus() {
            return endFicheContactStatus;
        }
    }
}
