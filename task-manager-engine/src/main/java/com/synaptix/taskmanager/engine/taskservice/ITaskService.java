package com.synaptix.taskmanager.engine.taskservice;

import com.synaptix.taskmanager.engine.listener.ITaskCycleListener;
import com.synaptix.taskmanager.engine.task.AbstractTask;

public interface ITaskService extends ITaskCycleListener {

	public IExecutionResult execute(AbstractTask task);

	public interface IExecutionResult {

		/**
		 * Task is finish
		 * 
		 * @return
		 */
		public boolean isFinished();

		/**
		 * Task no changes business object
		 * 
		 * @return
		 */
		public boolean isNoChanges();

		/**
		 * Get result of task, finish or not
		 * 
		 * @return
		 */
		public Object getResult();

		/**
		 * Must stop and restart task manager
		 * 
		 * @return
		 */
		public boolean mustStopAndRestartTaskManager();
	}
}
