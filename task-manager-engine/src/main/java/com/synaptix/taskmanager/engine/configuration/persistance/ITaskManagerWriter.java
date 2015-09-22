package com.synaptix.taskmanager.engine.configuration.persistance;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.synaptix.taskmanager.manager.AbstractTask;
import com.synaptix.taskmanager.manager.UpdateStatusTask;
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
	 * When taskCluster is finish (no task current)
	 * 
	 * @param taskCluster
	 */
	public ITaskCluster archiveTaskCluster(ITaskCluster taskCluster);

	public void saveNextTasksInTaskCluster(ITaskCluster taskCluster, AbstractTask toDoneTask, Object taskServiceResult, List<AbstractTask> nextCurrentTasks);

	public void saveNothingTask(ITaskCluster taskCluster, AbstractTask nothingTask, Object taskServiceResult, Throwable errorMessage);

	public void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, UpdateStatusTask toDoneTask, Object taskServiceResult, List<AbstractTask> newNextCurrentTasks);

}
