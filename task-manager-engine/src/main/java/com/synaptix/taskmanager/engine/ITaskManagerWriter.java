package com.synaptix.taskmanager.engine;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

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

	/**
	 * Save taskCluster, add taskNode for each taskObject
	 * 
	 * @param taskCluster
	 * @param taskObjectNodes
	 * @return
	 */
	public NewTaskObjectsInTaskClusterResult saveNewTaskObjectsInTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject<?>, TaskNode>> taskObjectNodes);

	public class NewTaskObjectsInTaskClusterResult {

		private final ITaskCluster taskCluster;

		private final List<ITask> tasks;

		public NewTaskObjectsInTaskClusterResult(ITaskCluster taskCluster, List<ITask> tasks) {
			super();
			this.taskCluster = taskCluster;
			this.tasks = tasks;
		}

		public ITaskCluster getTaskCluster() {
			return this.taskCluster;
		}

		public List<ITask> getTasks() {
			return this.tasks;
		}
	}

	/**
	 * When taskCluster is finish (no task current)
	 * 
	 * @param taskCluster
	 */
	public ITaskCluster archiveTaskCluster(ITaskCluster taskCluster);

	/**
	 * 
	 * @param taskCluster
	 * @param replaceTasks
	 * @param toDoneTasks
	 * @param toCurrentTasks
	 * @return
	 */
	public NextTasksInTaskClusterResult saveNextTasksInTaskCluster(ITaskCluster taskCluster, List<Pair<ITask, List<TaskNode>>> replaceTasks, List<ITask> toDoneTasks, List<ITask> toCurrentTasks);

	public ITask saveNothingTask(ITaskCluster taskCluster, ITask task);

	public class NextTasksInTaskClusterResult {

		private final ITaskCluster taskCluster;

		private final List<ITask> currentTasks;

		private final List<ITask> deleteTasks;

		public NextTasksInTaskClusterResult(ITaskCluster taskCluster, List<ITask> currentTasks, List<ITask> deleteTasks) {
			super();
			this.taskCluster = taskCluster;
			this.currentTasks = currentTasks;
			this.deleteTasks = deleteTasks;
		}

		public ITaskCluster getTaskCluster() {
			return taskCluster;
		}

		public List<ITask> getCurrentTasks() {
			return currentTasks;
		}

		public List<ITask> getDeleteTasks() {
			return deleteTasks;
		}
	}

	public class TaskNode {

		private final ITask task;

		private final List<TaskNode> childTaskNodes;

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
