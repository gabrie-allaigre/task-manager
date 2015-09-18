package com.synaptix.taskmanager.engine;

import java.util.List;

import com.synaptix.taskmanager.model.ITask;
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

	public List<ITask> saveNewTaskObjectInTaskCluster(ITaskCluster taskCluster, ITaskObject<?> taskObject, TaskNode taskNode);

	public void archiveCluster(ITaskCluster taskCluster);

	public void saveTask(ITask task);

	public void deleteTasksTodo(ITask task);

	public class TaskNode {

		private final ITask task;

		private List<TaskNode> childTaskNodes;

		public TaskNode(ITask task, List<TaskNode> childTaskNodes) {
			super();

			this.task = task;
			this.childTaskNodes = childTaskNodes;
		}

		public ITask getTask() {
			return task;
		}

		public List<TaskNode> getChildTaskNodes() {
			return childTaskNodes;
		}
	}
}
