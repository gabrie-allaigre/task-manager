package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.model.ITaskObject;

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
