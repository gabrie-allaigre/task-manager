package com.synaptix.taskmanager.engine.configuration.persistance;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

import java.util.List;

public interface ITaskManagerReader {

    /**
     * Find a task cluster by task object
     *
     * @param taskObject
     * @return
     */
    ITaskCluster findTaskClusterByTaskObject(ITaskObject taskObject);

    /**
     * Find all taskObjects by task cluster
     * <p>
     * note : used when taskCluster is not checkGraphCreated
     *
     * @param taskCluster
     * @return
     */
    List<? extends ITaskObject> findTaskObjectsByTaskCluster(ITaskCluster taskCluster);

    /**
     * Find all currents tasks for task cluster
     *
     * @param taskCluster
     * @return
     */
    List<? extends ICommonTask> findCurrentTasksByTaskCluster(ITaskCluster taskCluster);

    /**
     * Find next tasks for subTask
     *
     * @param subTask
     * @return
     */
    List<? extends ICommonTask> findNextTasksBySubTask(ISubTask subTask);

    /**
     * @param statusTask
     * @return
     */
    List<? extends ICommonTask> findOtherBranchFirstTasksByStatusTask(IStatusTask statusTask);

}
