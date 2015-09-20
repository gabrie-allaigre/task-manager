package com.synaptix.taskmanager.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.TaskStatus;
import com.synaptix.taskmanager.simple.SimpleTask;
import com.synaptix.taskmanager.simple.SimpleTaskCluster;

public class MemoryTaskManagerReaderWriter implements ITaskManagerReader, ITaskManagerWriter {

	private static final Log LOG = LogFactory.getLog(MemoryTaskManagerReaderWriter.class);

	private Map<ITaskCluster, List<ITaskObject<?>>> taskClusterMap;

	private Map<ITaskObject<?>, TaskNode> taskNodeMap;

	public MemoryTaskManagerReaderWriter() {
		super();

		this.taskClusterMap = new HashMap<ITaskCluster, List<ITaskObject<?>>>();
		this.taskNodeMap = new HashMap<ITaskObject<?>, TaskNode>();
	}

	// WRITER

	@Override
	public ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster) {
		LOG.info("saveNewTaskClusterForTaskObject " + taskCluster);
		taskClusterMap.put(taskCluster, new ArrayList<ITaskObject<?>>());
		return taskCluster;
	}

	@Override
	public NewTaskObjectsInTaskClusterResult saveNewTaskObjectsInTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject<?>, TaskNode>> taskObjectNodes) {
		LOG.info("saveNewTaskObjectInTaskCluster " + taskCluster);

		List<ITask> tasks = new ArrayList<ITask>();
		for (Pair<ITaskObject<?>, TaskNode> taskObjectNode : taskObjectNodes) {
			ITaskObject<?> taskObject = taskObjectNode.getLeft();
			taskClusterMap.get(taskCluster).add(taskObject);

			TaskNode taskNode = taskObjectNode.getRight();
			taskNodeMap.put(taskObject, taskNode);

			tasks.addAll(convertAllTaskNodesToTasks(Arrays.asList(taskNode)));
		}
		((SimpleTaskCluster) taskCluster).setCheckGraphCreated(true);

		return new NewTaskObjectsInTaskClusterResult(taskCluster, tasks);
	}

	@Override
	public ITaskCluster archiveTaskCluster(ITaskCluster taskCluster) {
		LOG.info("archiveTaskCluster " + taskCluster);
		((SimpleTaskCluster) taskCluster).setCheckArchived(true);
		return taskCluster;
	}

	@Override
	public NextTasksInTaskClusterResult saveNextTasksInTaskCluster(ITaskCluster taskCluster, List<Pair<ITask, List<TaskNode>>> replaceTasks, List<ITask> toDoneTasks, List<ITask> toCurrentTasks) {
		LOG.info("saveNextTasksInTaskCluster " + taskCluster);

		List<ITask> tasks = new ArrayList<ITask>();
		List<ITask> deleteTasks = new ArrayList<ITask>();

		if (replaceTasks != null && !replaceTasks.isEmpty()) {
			for (Pair<ITask, List<TaskNode>> replaceTask : replaceTasks) {

			}
		}

		if (toDoneTasks != null && !toDoneTasks.isEmpty()) {
			for (ITask task : toDoneTasks) {
				((SimpleTask) task).setTaskStatus(TaskStatus.DONE);
				tasks.add(task);
			}
		}

		if (toCurrentTasks != null && !toCurrentTasks.isEmpty()) {
			for (ITask task : toCurrentTasks) {
				((SimpleTask) task).setTaskStatus(TaskStatus.CURRENT);
				tasks.add(task);
			}
		}

		return new NextTasksInTaskClusterResult(taskCluster, tasks, deleteTasks);
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
	public List<ITask> findCurrentTasksByTaskCluster(ITaskCluster taskCluster) {
		List<ITask> tasks = new ArrayList<ITask>();
		List<ITaskObject<?>> taskObjects = taskClusterMap.get(taskCluster);
		if (taskObjects != null && !taskObjects.isEmpty()) {
			for (ITaskObject<?> taskObject : taskObjects) {
				TaskNode taskNode = taskNodeMap.get(taskObject);
				if (taskNode != null) {
					tasks.addAll(findTasksForStatus(taskNode, TaskStatus.CURRENT));
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

	@Override
	public List<ITask> findNextTodoTasksByTaskClusterTask(ITaskCluster taskCluster, ITask task) {
		List<ITaskObject<?>> taskObjects = taskClusterMap.get(taskCluster);
		if (taskObjects != null && !taskObjects.isEmpty()) {
			for (ITaskObject<?> taskObject : taskObjects) {
				TaskNode taskNode = taskNodeMap.get(taskObject);
				TaskNode tn = findTaskNodeWithTask(taskNode, task);
				if (tn != null) {
					return convertTaskNodesToTasks(tn.getChildTaskNodes());
				}
			}
		}
		return null;
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

	private List<ITask> convertTaskNodesToTasks(List<TaskNode> taskNodes) {
		List<ITask> res = new ArrayList<ITask>();
		if (taskNodes != null && !taskNodes.isEmpty()) {
			for (TaskNode taskNode : taskNodes) {
				res.add(taskNode.getTask());
			}
		}
		return res;
	}

	private List<ITask> convertAllTaskNodesToTasks(List<TaskNode> taskNodes) {
		List<ITask> res = new ArrayList<ITask>();
		if (taskNodes != null && !taskNodes.isEmpty()) {
			for (TaskNode taskNode : taskNodes) {
				res.add(taskNode.getTask());
				res.addAll(convertAllTaskNodesToTasks(taskNode.getChildTaskNodes()));
			}
		}
		return res;
	}
}
