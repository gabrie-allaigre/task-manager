package com.synaptix.taskmanager.engine.taskservice;

import com.synaptix.taskmanager.engine.listener.ITaskCycleListener;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskService extends ITaskCycleListener {

	/**
	 * Execute service
	 *
	 * @param task task link with service
	 * @return result of execution, <use>ExecutionResultBuilder</use>
	 */
	IExecutionResult execute(AbstractTask task);

	interface Context {

		void addTaskObjectsToTaskCluster(ITaskCluster taskCluster, ITaskObject<?>... taskObjects);

		void removeTaskObjectsFromTaskCluster(ITaskObject<?>... taskObjects);

		ITaskCluster moveTaskObjectsToNewTaskCluster(ITaskObject<?>... taskObjects);

		void moveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, ITaskObject<?>... taskObjects);

	}

	interface IExecutionResult {

		/**
		 * Task is finish
		 *
		 * @return true is finish
		 */
		boolean isFinished();

		/**
		 * Task no changes business object
		 *
		 * @return true is no changes
		 */
		boolean isNoChanges();

		/**
		 * Get result of task, finish or not
		 *
		 * @return the result
		 */
		Object getResult();

		/**
		 * Must stop and restart task manager
		 *
		 * @return true if must stop and restart
		 */
		boolean mustStopAndRestartTaskManager();
	}
}
