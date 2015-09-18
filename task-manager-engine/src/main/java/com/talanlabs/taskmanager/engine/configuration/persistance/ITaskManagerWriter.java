package com.talanlabs.taskmanager.engine.configuration.persistance;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.task.IStatusTask;
import com.talanlabs.taskmanager.engine.task.ISubTask;
import com.talanlabs.taskmanager.model.ITaskCluster;
import com.talanlabs.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface ITaskManagerWriter {

    /**
     * Save a new task cluster for task object and save all tasks
     *
     * @param taskCluster new taskCluster
     * @return cluster
     */
    ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster);

    /**
     * Save taskCluster, add taskNode for each taskObject
     *
     * @param taskCluster     task cluster
     * @param taskObjectTasks list of task object, status task
     * @return cluster
     */
    ITaskCluster saveNewGraphFromTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject, IStatusTask>> taskObjectTasks);

    /**
     * Save remove taskObjects to task cluster
     *
     * @param taskCluster task cluster
     * @param taskObjects task object
     */
    void saveRemoveTaskObjectsFromTaskCluster(ITaskCluster taskCluster, List<ITaskObject> taskObjects);

    /**
     * Save move task objects to task cluster
     *
     * @param dstTaskCluster   destination cluster
     * @param modifyClusterMap map to move
     * @return cluster
     */
    ITaskCluster saveMoveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, Map<ITaskCluster, List<ITaskObject>> modifyClusterMap);

    /**
     * When taskCluster is finish (no task current)
     *
     * @param taskCluster cluster
     * @return cluster same cluster
     */
    ITaskCluster archiveTaskCluster(ITaskCluster taskCluster);

    /**
     * Save next tasks in task cluster
     *
     * @param taskCluster       cluster
     * @param toDoneTask        task is done
     * @param taskServiceResult result
     * @param nextCurrentTasks  next tasks
     */
    void saveNextTasksInTaskCluster(ITaskCluster taskCluster, ICommonTask toDoneTask, Object taskServiceResult, List<ICommonTask> nextCurrentTasks);

    /**
     * Save new task after finish status task in cluster
     *
     * @param taskCluster              cluster
     * @param toDoneTask               done task
     * @param taskServiceResult        service result
     * @param newTasks                 new tasks
     * @param linkNextTasksMap         link between sub tasks
     * @param otherBranchFirstTasksMap other task
     * @param nextCurrentTasks         next current tasks
     * @param deleteTasks              delete tasks other node
     */
    void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, IStatusTask toDoneTask, Object taskServiceResult, List<ICommonTask> newTasks, Map<ISubTask, List<ICommonTask>> linkNextTasksMap,
            Map<IStatusTask, List<ICommonTask>> otherBranchFirstTasksMap, List<ICommonTask> nextCurrentTasks, List<ICommonTask> deleteTasks);

    /**
     * Task is nothing
     *
     * @param taskCluster       cluster
     * @param nothingTask       current task
     * @param taskServiceResult result
     * @param errorMessage      error
     */
    void saveNothingTask(ITaskCluster taskCluster, ICommonTask nothingTask, Object taskServiceResult, Throwable errorMessage);

}
