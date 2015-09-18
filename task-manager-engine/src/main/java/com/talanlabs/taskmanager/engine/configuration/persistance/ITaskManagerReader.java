package com.talanlabs.taskmanager.engine.configuration.persistance;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.task.IStatusTask;
import com.talanlabs.taskmanager.engine.task.ISubTask;
import com.talanlabs.taskmanager.model.ITaskCluster;
import com.talanlabs.taskmanager.model.ITaskObject;

import java.util.List;

public interface ITaskManagerReader {

    /**
     * Find a task cluster by task object
     *
     * @param taskObject task object
     * @return cluster
     */
    ITaskCluster findTaskClusterByTaskObject(ITaskObject taskObject);

    /**
     * Find all taskObjects by task cluster
     * <p>
     * note : used when taskCluster is not checkGraphCreated
     *
     * @param taskCluster cluster
     * @return list of task object
     */
    List<? extends ITaskObject> findTaskObjectsByTaskCluster(ITaskCluster taskCluster);

    /**
     * Find all currents tasks for task cluster
     *
     * @param taskCluster cluster
     * @return list of common task
     */
    List<? extends ICommonTask> findCurrentTasksByTaskCluster(ITaskCluster taskCluster);

    /**
     * Find next tasks for subTask
     *
     * @param subTask        sub task
     * @param uniquePossible unique only
     * @return list of common task
     */
    List<? extends ICommonTask> findNextTasksBySubTask(ISubTask subTask, boolean uniquePossible);

    /**
     * @param statusTask status task
     * @return list of common task
     */
    List<? extends ICommonTask> findOtherBranchFirstTasksByStatusTask(IStatusTask statusTask);

}
