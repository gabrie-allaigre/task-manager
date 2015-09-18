package com.talanlabs.taskmanager.engine.memory;

import com.talanlabs.taskmanager.engine.task.ISubTask;
import com.talanlabs.taskmanager.model.ITaskObject;

public class SimpleSubTask extends AbstractSimpleCommonTask implements ISubTask {

    private ITaskObject taskObject;

    public SimpleSubTask(String codeTaskDefinition) {
        super(codeTaskDefinition);
    }

    @Override
    public String toString() {
        return "SimpleSubTask -> " + getCodeTaskDefinition();
    }
}
