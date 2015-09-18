package com.talanlabs.taskmanager.engine.configuration.factory;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.task.IStatusTask;
import com.talanlabs.taskmanager.engine.task.ISubTask;
import com.talanlabs.taskmanager.model.ITaskCluster;
import com.talanlabs.taskmanager.model.ITaskObject;

public interface ITaskFactory {

    /**
     * Create a new Task cluster
     *
     * @return
     */
    ITaskCluster newTaskCluster();

    /**
     * Create a new task
     *
     * @return
     */
    ISubTask newSubTask(String codeSubTaskDefinition);

    /**
     * Task is sub task
     *
     * @param commonTask
     * @return true if sub task
     */
    boolean isSubTask(ICommonTask commonTask);

    /**
     * Create a update status task
     *
     * @return
     */
    IStatusTask newStatusTask(String codeStatusTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus);

    /**
     * Task is status task
     *
     * @param commonTask
     * @return true if status task
     */
    boolean isStatusTask(ICommonTask commonTask);

}
