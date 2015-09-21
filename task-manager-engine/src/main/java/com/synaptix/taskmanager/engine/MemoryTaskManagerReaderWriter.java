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

			tasks.addAll(convertAllTaskNodesToTasks(taskObject, Arrays.asList(taskNode)));
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

		List<ITask> currentTasks = new ArrayList<ITask>();
		List<ITask> deleteTasks = new ArrayList<ITask>();

		if (replaceTasks != null && !replaceTasks.isEmpty()) {
			for (Pair<ITask, List<TaskNode>> replaceTask : replaceTasks) {
				ITask task = replaceTask.getLeft();
				List<TaskNode> newChilds = replaceTask.getRight();

				TaskNode taskNode = taskNodeMap.get(((SimpleTask) task).getTaskObject());

				TaskNode groupTaskNode = findTaskNodeWithTask(taskNode, replaceTask.getLeft());

				List<TaskNode> parents = findParentTaskNodesWithTaskNode(taskNode, groupTaskNode);

				// Si le groupe a remplacer par des nouveaux noeuds
				if (newChilds != null && !newChilds.isEmpty()) {
					convertAllTaskNodesToTasks(((SimpleTask) task).getTaskObject(), newChilds);

					for (TaskNode parent : parents) {
						parent.getChildTaskNodes().remove(groupTaskNode);
						parent.getChildTaskNodes().addAll(replaceTask.getRight());
					}

					for (TaskNode newChild : newChilds) {
						List<TaskNode> leafs = findLastTaskNodesWithTaskNode(newChild);
						for (TaskNode leaf : leafs) {
							leaf.getChildTaskNodes().addAll(groupTaskNode.getChildTaskNodes());
						}
					}
				} else {
					// Les parents du group associé au fils du groupe
					for (TaskNode parent : parents) {
						parent.getChildTaskNodes().remove(groupTaskNode);
						parent.getChildTaskNodes().addAll(groupTaskNode.getChildTaskNodes());
					}
				}

				// On passe les noeux à current
				for (TaskNode parent : parents) {
					for (TaskNode child : parent.getChildTaskNodes()) {
						((SimpleTask) child.getTask()).setTaskStatus(TaskStatus.CURRENT);
						currentTasks.add(child.getTask());
					}
				}

				deleteTasks.add(groupTaskNode.getTask());
			}
		}

		if (toDoneTasks != null && !toDoneTasks.isEmpty()) {
			for (ITask task : toDoneTasks) {
				((SimpleTask) task).setTaskStatus(TaskStatus.DONE);
			}
		}

		if (toCurrentTasks != null && !toCurrentTasks.isEmpty()) {
			for (ITask task : toCurrentTasks) {
				((SimpleTask) task).setTaskStatus(TaskStatus.CURRENT);
				currentTasks.add(task);
			}
		}

		return new NextTasksInTaskClusterResult(taskCluster, currentTasks, deleteTasks);
	}

	@Override
	public ITask saveNothingTask(ITaskCluster taskCluster, ITask task) {
		return task;
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
}
