package com.synaptix.taskmanager.engine.configuration.factory;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

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
     * @param task
     * @return true if sub task
     */
    boolean isSubTask(ICommonTask task);

    /**
     * Create a update status task
     *
     * @return
     */
    IStatusTask newStatusTask(String codeStatusTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus);

    /**
     * Task is status task
     *
     * @param task
     * @return true if status task
     */
    boolean isStatusTask(ICommonTask task);

}
