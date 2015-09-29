package com.synaptix.taskmanager.engine.configuration.persistance;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.engine.task.IGeneralTask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface ITaskManagerWriter {

	/**
	 * Save a new task cluster for task object and save all tasks
	 *
	 * @param taskCluster new taskCluster
	 */
	ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster);

	/**
	 * Save taskCluster, add taskNode for each taskObject
	 *
	 * @param taskCluster
	 * @param taskObjectTasks
	 */
	ITaskCluster saveNewGraphFromTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject, IGeneralTask>> taskObjectTasks);

	/**
	 * Save remove taskObjects to task cluster
	 *
	 * @param taskCluster
	 * @param taskObjects
	 */
	void saveRemoveTaskObjectsFromTaskCluster(ITaskCluster taskCluster, List<ITaskObject> taskObjects);

	/**
	 * Save move task objects to task cluster
	 *
	 * @param dstTaskCluster
	 * @param modifyClusterMap
	 * @param newTaskCluster
	 * @return
	 */
	ITaskCluster saveMoveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, Map<ITaskCluster, List<ITaskObject>> modifyClusterMap, boolean newTaskCluster);

	/**
	 * When taskCluster is finish (no task current)
	 *
	 * @param taskCluster
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
	 * @param taskCluster
	 * @param toDoneTask
	 * @param taskServiceResult
	 * @param newTasks
	 * @param linkNextTasksMap
	 * @param otherBranchFirstTasksMap
	 * @param nextCurrentTasks
	 * @param deleteTasks
	 */
	void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, IGeneralTask toDoneTask, Object taskServiceResult, List<ICommonTask> newTasks,
			Map<ISubTask, List<ICommonTask>> linkNextTasksMap,  Map<IGeneralTask, List<ICommonTask>> otherBranchFirstTasksMap,List<ICommonTask> nextCurrentTasks,
			List<ICommonTask> deleteTasks);

	/**
	 * Task is nothing
	 *
	 * @param taskCluster
	 * @param nothingTask
	 * @param taskServiceResult
	 * @param errorMessage
	 */
	void saveNothingTask(ITaskCluster taskCluster, ICommonTask nothingTask, Object taskServiceResult, Throwable errorMessage);

}
