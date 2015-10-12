package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.model.ITaskObject;

import java.util.ArrayList;
import java.util.List;

public class SimpleStatusTask extends AbstractSimpleCommonTask implements IStatusTask {

    private final List<ICommonTask> otherBranchFirstTasks;

    private final Class<? extends ITaskObject> taskObjectClass;

    private final Object currentStatus;

    public SimpleStatusTask(String codeTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
        super(codeTaskDefinition);

        this.taskObjectClass = taskObjectClass;
        this.currentStatus = currentStatus;

        this.otherBranchFirstTasks = new ArrayList<>();
    }

    @Override
    public <F extends ITaskObject> Class<F> getTaskObjectClass() {
        return (Class<F>) taskObjectClass;
    }

    @Override
    public <E> E getCurrentStatus() {
        return (E) currentStatus;
    }

    public final List<ICommonTask> getOtherBranchFirstTasks() {
        return otherBranchFirstTasks;
    }

    @Override
    public String toString() {
        return "SimpleStatusTask -> " + getCodeTaskDefinition() + " " + getCurrentStatus();
    }
}
