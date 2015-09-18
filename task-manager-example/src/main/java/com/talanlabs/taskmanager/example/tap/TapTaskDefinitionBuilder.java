package com.talanlabs.taskmanager.example.tap;

import com.talanlabs.taskmanager.engine.taskdefinition.DefaultTaskDefinition;
import com.talanlabs.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.talanlabs.taskmanager.engine.taskservice.ITaskService;
import com.talanlabs.taskmanager.example.tap.model.FicheContactStatus;

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

    private static class MyTapTaskDefinition extends DefaultTaskDefinition implements ITapTaskDefinition {

        private String type;

        private FicheContactStatus endFicheContactStatus;

        public MyTapTaskDefinition(String code, ITaskService taskService) {
            super(code,taskService);
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
