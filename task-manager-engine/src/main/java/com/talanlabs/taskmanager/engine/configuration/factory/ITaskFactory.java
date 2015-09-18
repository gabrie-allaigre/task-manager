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
     * @return a new task cluster
     */
    ITaskCluster newTaskCluster();

    /**
     * Create a new task
     *
     * @param codeSubTaskDefinition code definition
     * @return a new sub task
     */
    ISubTask newSubTask(String codeSubTaskDefinition);

    /**
     * Task is sub task
     *
     * @param commonTask parent task
     * @return true if sub task
     */
    boolean isSubTask(ICommonTask commonTask);

    /**
     * Create a update status task
     *
     * @param codeStatusTaskDefinition code definition
     * @param taskObjectClass          task object class
     * @param currentStatus            status
     * @return a new status task
     */
    IStatusTask newStatusTask(String codeStatusTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus);

    /**
     * Task is status task
     *
     * @param commonTask parent task
     * @return true if status task
     */
    boolean isStatusTask(ICommonTask commonTask);

}
