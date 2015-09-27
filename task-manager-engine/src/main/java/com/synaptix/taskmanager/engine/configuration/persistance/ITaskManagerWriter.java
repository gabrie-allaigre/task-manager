package com.synaptix.taskmanager.engine.configuration.persistance;

import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.task.UpdateStatusTask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface ITaskManagerWriter {

	/**
	 * Save a new task cluster for task object and save all tasks
	 * 
	 * @param taskCluster
	 *            new taskCluster
	 */
	ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster);

	/**
	 * Save taskCluster, add taskNode for each taskObject
	 * 
	 * @param taskCluster
	 * @param taskObjectTasks
	 */
	ITaskCluster saveNewGraphFromTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject, UpdateStatusTask>> taskObjectTasks);

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
	ITaskCluster saveMoveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, Map<ITaskCluster, List<ITaskObject>> modifyClusterMap,boolean newTaskCluster);

	/**
	 * When taskCluster is finish (no task current)
	 * 
	 * @param taskCluster
	 */
	ITaskCluster archiveTaskCluster(ITaskCluster taskCluster);

	/**
	 * Save next tasks in task cluster
	 * 
	 * @param taskCluster
	 *            cluster
	 * @param toDoneTask
	 *            task is done
	 * @param taskServiceResult
	 *            result
	 * @param nextCurrentTasks
	 *            next tasks
	 */
	void saveNextTasksInTaskCluster(ITaskCluster taskCluster, AbstractTask toDoneTask, Object taskServiceResult, List<AbstractTask> nextCurrentTasks);

	/**
	 * Task is nothing
	 * 
	 * @param taskCluster
	 * @param nothingTask
	 * @param taskServiceResult
	 * @param errorMessage
	 */
	void saveNothingTask(ITaskCluster taskCluster, AbstractTask nothingTask, Object taskServiceResult, Throwable errorMessage);

	/**
	 * 
	 * @param taskCluster
	 * @param toDoneTask
	 * @param taskServiceResult
	 * @param newNextCurrentTasks
	 * @param deleteTasks
	 */
	void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, UpdateStatusTask toDoneTask, Object taskServiceResult, List<AbstractTask> newNextCurrentTasks, List<AbstractTask> deleteTasks);


}
