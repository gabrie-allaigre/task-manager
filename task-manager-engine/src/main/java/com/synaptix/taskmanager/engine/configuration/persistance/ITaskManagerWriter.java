package com.synaptix.taskmanager.engine.configuration.persistance;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.task.UpdateStatusTask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskManagerWriter {

	/**
	 * Save a new task cluster for task object and save all tasks
	 * 
	 * @param taskCluster
	 *            new taskCluster
	 */
	public ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster);

	/**
	 * Save taskCluster, add taskNode for each taskObject
	 * 
	 * @param taskCluster
	 * @param taskObjectTasks
	 */
	public ITaskCluster saveNewGraphForTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject<?>, UpdateStatusTask>> taskObjectTasks);

	/**
	 *
	 * @param taskCluster
	 * @param taskObjects
	 */
	public void saveRemoveTaskObjectsForTaskCluster(ITaskCluster taskCluster,List<ITaskObject<?>> taskObjects);

	/**
	 * When taskCluster is finish (no task current)
	 * 
	 * @param taskCluster
	 */
	public ITaskCluster archiveTaskCluster(ITaskCluster taskCluster);

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
	public void saveNextTasksInTaskCluster(ITaskCluster taskCluster, AbstractTask toDoneTask, Object taskServiceResult, List<AbstractTask> nextCurrentTasks);

	/**
	 * Task is nothing
	 * 
	 * @param taskCluster
	 * @param nothingTask
	 * @param taskServiceResult
	 * @param errorMessage
	 */
	public void saveNothingTask(ITaskCluster taskCluster, AbstractTask nothingTask, Object taskServiceResult, Throwable errorMessage);

	/**
	 * 
	 * @param taskCluster
	 * @param toDoneTask
	 * @param taskServiceResult
	 * @param newNextCurrentTasks
	 * @param deleteTasks
	 */
	public void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, UpdateStatusTask toDoneTask, Object taskServiceResult, List<AbstractTask> newNextCurrentTasks, List<AbstractTask> deleteTasks);

}
