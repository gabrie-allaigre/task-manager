package com.talanlabs.taskmanager.jpa;

import com.talanlabs.taskmanager.engine.configuration.factory.AbstractTaskFactory;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.task.IStatusTask;
import com.talanlabs.taskmanager.engine.task.ISubTask;
import com.talanlabs.taskmanager.jpa.model.Cluster;
import com.talanlabs.taskmanager.jpa.model.IBusinessTaskObject;
import com.talanlabs.taskmanager.jpa.model.Task;
import com.talanlabs.taskmanager.model.ITaskCluster;
import com.talanlabs.taskmanager.model.ITaskObject;

public class JPATaskFactory extends AbstractTaskFactory {

    private final ICurrentStatusTransform currentStatusTransform;

    public JPATaskFactory() {
        this(StringCurrentStatusTransform.INSTANCE);
    }

    public JPATaskFactory(ICurrentStatusTransform currentStatusTransform) {
        super();

        this.currentStatusTransform = currentStatusTransform;
    }

    @Override
    public ITaskCluster newTaskCluster() {
        return new Cluster();
    }

    @Override
    public ISubTask newSubTask(String codeSubTaskDefinition) {
        Task task = new Task();
        task.setType(Task.Type.SUB_TASK);
        task.setCodeTaskDefinition(codeSubTaskDefinition);
        return new JPATask(currentStatusTransform, task);
    }

    @Override
    public boolean isSubTask(ICommonTask commonTask) {
        return Task.Type.SUB_TASK.equals(((JPATask) commonTask).getTask().getType());
    }

    @Override
    public IStatusTask newStatusTask(String codeStatusTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
        Task task = new Task();
        task.setType(Task.Type.STATUS_TASK);
        task.setCodeTaskDefinition(codeStatusTaskDefinition);
        task.setBusinessTaskObjectClass((Class<? extends IBusinessTaskObject>) taskObjectClass);
        task.setCurrentStatus(currentStatusTransform.toString(taskObjectClass,currentStatus));
        return new JPATask(currentStatusTransform, task);
    }

    @Override
    public boolean isStatusTask(ICommonTask commonTask) {
        return Task.Type.STATUS_TASK.equals(((JPATask) commonTask).getTask().getType());
    }
}
