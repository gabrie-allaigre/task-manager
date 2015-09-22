package com.synaptix.taskmanager.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.synaptix.taskmanager.manager.AbstractTask;
import com.synaptix.taskmanager.manager.UpdateStatusTask;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.TaskStatus;

public class MemoryTaskManagerReaderWriter implements ITaskManagerReader, ITaskManagerWriter {

	private static final Log LOG = LogFactory.getLog(MemoryTaskManagerReaderWriter.class);

	private Map<ITaskCluster, List<ITaskObject<?>>> taskClusterMap;

	private Map<ITaskObject<?>, List<AbstractTask>> taskNodeMap;

	public MemoryTaskManagerReaderWriter() {
		super();

		this.taskClusterMap = new HashMap<ITaskCluster, List<ITaskObject<?>>>();
		this.taskNodeMap = new HashMap<ITaskObject<?>, List<AbstractTask>>();
	}

	// WRITER

	@Override
	public ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster) {
		LOG.info("MRW - saveNewTaskClusterForTaskObject");
		taskClusterMap.put(taskCluster, new ArrayList<ITaskObject<?>>());
		return taskCluster;
	}

	@Override
	public ITaskCluster saveNewGraphForTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject<?>, UpdateStatusTask>> taskObjectTasks) {
		LOG.info("MRW - saveNewTaskObjectInTaskCluster");

		for (Pair<ITaskObject<?>, UpdateStatusTask> taskObjectNode : taskObjectTasks) {
			ITaskObject<?> taskObject = taskObjectNode.getLeft();
			taskClusterMap.get(taskCluster).add(taskObject);

			UpdateStatusTask taskNode = taskObjectNode.getRight();

			((SimpleUpdateStatusTask) taskNode).setTaskObject(taskObject);
			taskNodeMap.put(taskObject, Arrays.<AbstractTask> asList(taskNode));
		}

		return taskCluster;
	}

	@Override
	public ITaskCluster archiveTaskCluster(ITaskCluster taskCluster) {
		LOG.info("MRW - archiveTaskCluster " + taskCluster);
		((SimpleTaskCluster) taskCluster).setCheckArchived(true);
		return taskCluster;
	}

	@Override
	public void saveNextTasksInTaskCluster(ITaskCluster taskCluster, AbstractTask toDoneTask, Object taskServiceResult, List<AbstractTask> nextCurrentTasks) {
		LOG.info("MRW - saveNextTasksInTaskCluster");

	}

	@Override
	public void saveNothingTask(ITaskCluster taskCluster, AbstractTask nothingTask, Object taskServiceResult, Throwable errorMessage) {
		LOG.info("MRW - saveNothingTask");
	}

	// READER

	@Override
	public ITaskCluster findTaskClusterByTaskObject(ITaskObject<?> taskObject) {
		for (Entry<ITaskCluster, List<ITaskObject<?>>> entry : taskClusterMap.entrySet()) {
			if (entry.getValue().contains(taskObject)) {
				return entry.getKey();
			}
		}
		return null;
	}

	@Override
	public List<ITaskObject<?>> findTaskObjectsByTaskCluster(ITaskCluster taskCluster) {
		return taskClusterMap.get(taskCluster);
	}

	@Override
	public List<AbstractTask> findCurrentTasksByTaskCluster(ITaskCluster taskCluster) {
		List<AbstractTask> tasks = new ArrayList<AbstractTask>();
		List<ITaskObject<?>> taskObjects = taskClusterMap.get(taskCluster);
		if (taskObjects != null && !taskObjects.isEmpty()) {
			for (ITaskObject<?> taskObject : taskObjects) {
				List<AbstractTask> taskNode = taskNodeMap.get(taskObject);
				if (taskNode != null) {
					tasks.addAll(taskNode);
				}
			}
		}
		return tasks;
	}

	private List<ITask> findTasksForStatus(TaskNode taskNode, TaskStatus taskStatus) {
		List<ITask> res = new ArrayList<ITask>();
		if (taskStatus.equals(taskNode.getTask().getTaskStatus())) {
			res.add(taskNode.getTask());
		}
		if (taskNode.getChildTaskNodes() != null && !taskNode.getChildTaskNodes().isEmpty()) {
			for (TaskNode child : taskNode.getChildTaskNodes()) {
				res.addAll(findTasksForStatus(child, taskStatus));
			}
		}
		return res;
	}

	private TaskNode findTaskNodeWithTask(TaskNode taskNode, ITask task) {
		if (task.equals(taskNode.getTask())) {
			return taskNode;
		}
		if (taskNode.getChildTaskNodes() != null && !taskNode.getChildTaskNodes().isEmpty()) {
			for (TaskNode child : taskNode.getChildTaskNodes()) {
				TaskNode res = findTaskNodeWithTask(child, task);
				if (res != null) {
					return res;
				}
			}
		}
		return null;
	}

	private List<TaskNode> findParentTaskNodesWithTaskNode(TaskNode taskNode, TaskNode childTaskNode) {
		List<TaskNode> res = new ArrayList<TaskNode>();
		if (taskNode.getChildTaskNodes() != null && !taskNode.getChildTaskNodes().isEmpty()) {
			for (TaskNode child : taskNode.getChildTaskNodes()) {
				if (child.equals(childTaskNode)) {
					res.add(taskNode);
				}
				res.addAll(findParentTaskNodesWithTaskNode(child, childTaskNode));
			}
		}
		return res;
	}

	private List<TaskNode> findLastTaskNodesWithTaskNode(TaskNode taskNode) {
		List<TaskNode> res = new ArrayList<TaskNode>();
		if (taskNode.getChildTaskNodes() != null && !taskNode.getChildTaskNodes().isEmpty()) {
			for (TaskNode child : taskNode.getChildTaskNodes()) {
				res.addAll(findLastTaskNodesWithTaskNode(child));
			}
		} else {
			res.add(taskNode);
		}
		return res;
	}

	private List<ITask> convertTaskNodesToTasks(List<TaskNode> taskNodes) {
		List<ITask> res = new ArrayList<ITask>();
		if (taskNodes != null && !taskNodes.isEmpty()) {
			for (TaskNode taskNode : taskNodes) {
				res.add(taskNode.getTask());
			}
		}
		return res;
	}

	private List<ITask> convertAllTaskNodesToTasks(ITaskObject<?> taskObject, List<TaskNode> taskNodes) {
		List<ITask> res = new ArrayList<ITask>();
		if (taskNodes != null && !taskNodes.isEmpty()) {
			for (TaskNode taskNode : taskNodes) {
				((SimpleTask) taskNode.getTask()).setTaskObject(taskObject);
				res.add(taskNode.getTask());
				res.addAll(convertAllTaskNodesToTasks(taskObject, taskNode.getChildTaskNodes()));
			}
		}
		return res;
	}

	public static class TaskNode {

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
