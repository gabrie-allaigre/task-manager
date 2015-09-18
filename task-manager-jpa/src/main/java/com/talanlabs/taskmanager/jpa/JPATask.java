package com.talanlabs.taskmanager.jpa;

import com.talanlabs.taskmanager.engine.task.IStatusTask;
import com.talanlabs.taskmanager.engine.task.ISubTask;
import com.talanlabs.taskmanager.jpa.model.Task;
import com.talanlabs.taskmanager.model.ITaskObject;

public class JPATask implements IStatusTask, ISubTask {

    private final ICurrentStatusTransform currentStatusTransform;

    private final Task task;

    public JPATask(ICurrentStatusTransform currentStatusTransform, Task task) {
        super();

        this.currentStatusTransform = currentStatusTransform;
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    @Override
    public <F extends ITaskObject> Class<F> getTaskObjectClass() {
        return (Class<F>) task.getTaskObjectClass();
    }

    @Override
    public <E> E getCurrentStatus() {
        return (E)currentStatusTransform.toObject(task.getTaskObjectClass(),task.getCurrentStatus());
    }

    @Override
    public String getCodeTaskDefinition() {
        return task.getCodeTaskDefinition();
    }

    @Override
    public int hashCode() {
        return task.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null || this.getClass() != obj.getClass()) && (task != null && ((JPATask) obj).task != null ? task.equals(((JPATask) obj).task) : super.equals(obj));
    }

    @Override
    public String toString() {
        return task.toString();
    }
}
