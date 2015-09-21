package com.synaptix.taskmanager.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.component.model.IServiceResult;
import com.synaptix.component.model.IStackResult;
import com.synaptix.taskmanager.antlr.AbstractGraphNode;
import com.synaptix.taskmanager.antlr.GraphCalcHelper;
import com.synaptix.taskmanager.antlr.IdGraphNode;
import com.synaptix.taskmanager.antlr.NextGraphNode;
import com.synaptix.taskmanager.antlr.ParallelGraphNode;
import com.synaptix.taskmanager.engine.ITaskManagerWriter.NewTaskObjectsInTaskClusterResult;
import com.synaptix.taskmanager.engine.ITaskManagerWriter.NextTasksInTaskClusterResult;
import com.synaptix.taskmanager.engine.ITaskManagerWriter.TaskNode;
import com.synaptix.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.synaptix.taskmanager.engine.configuration.result.ServiceResultBuilder;
import com.synaptix.taskmanager.error.TaskManagerErrorEnum;
import com.synaptix.taskmanager.manager.ITaskObjectManager;
import com.synaptix.taskmanager.manager.taskdefinition.ITaskDefinition;
import com.synaptix.taskmanager.manager.taskservice.ITaskService;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.model.domains.TaskStatus;

public class TaskManagerEngine {

	private static final Log LOG = LogFactory.getLog(TaskManagerEngine.class);

	private ITaskManagerConfiguration taskManagerConfiguration;

	public TaskManagerEngine(ITaskManagerConfiguration taskManagerConfiguration) {
		super();

		this.taskManagerConfiguration = taskManagerConfiguration;
	}

	public ITaskManagerConfiguration getTaskManagerConfiguration() {
		return taskManagerConfiguration;
	}

	// Start engine

	/**
	 * Starts engine, creates cluster if cluster does not exist
	 * 
	 * @param taskObject
	 * @return
	 */
	public IServiceResult<Void> startEngine(ITaskObject<?> taskObject) {
		if (taskObject == null) {
			return new ServiceResultBuilder<TaskManagerErrorEnum>().compileResult(null);
		}
		// If cluster not existe, create
		ITaskCluster taskCluster = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
		if (taskCluster == null) {
			taskCluster = createTaskCluster(taskObject);
		}

		return startEngine(taskCluster);
	}

	/**
	 * 
	 * @param taskCluster
	 * @return
	 */
	public IServiceResult<Void> startEngine(ITaskCluster taskCluster) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - StartEngine");
		}

		if (taskCluster == null || taskCluster.isCheckArchived()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - Nothing, cluster is null or archived");
			}
			return new ServiceResultBuilder<TaskManagerErrorEnum>().compileResult(null);
		}

		ServiceResultBuilder<TaskManagerErrorEnum> serviceResultBuilder = new ServiceResultBuilder<TaskManagerErrorEnum>();

		boolean restart = false;

		Set<ITaskCluster> restartClusters = new HashSet<ITaskCluster>();

		// Find all current task for cluster
		List<ITask> tasks = getTaskManagerConfiguration().getTaskManagerReader().findCurrentTasksByTaskCluster(taskCluster);

		if (tasks == null || tasks.isEmpty()) {
			if (taskCluster != null && !taskCluster.isCheckGraphCreated()) {
				createTaskGraphs(taskCluster);
				restartClusters.add(taskCluster);
			} else {
				getTaskManagerConfiguration().getTaskManagerWriter().archiveTaskCluster(taskCluster);
			}
		} else {
			LinkedList<ITask> tasksQueue = new LinkedList<ITask>(tasks);
			List<ITask> recycleList = new ArrayList<ITask>();

			while (!tasksQueue.isEmpty()) {
				ITask task = tasksQueue.removeFirst();

				boolean done = true;
				Throwable errorMessage = null;
				ITaskService taskService = null;
				if (task.getServiceCode() != null) {
					ITaskDefinition taskDefinition = getTaskManagerConfiguration().getTaskDefinitionRegistry().getTaskDefinition(task.getServiceCode());
					taskService = taskDefinition.getTaskService();
					if (taskService == null) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("TM - taskService does not exist");
						}
						errorMessage = new NotFoundTaskDefinitionException();
					} else {
						if (LOG.isDebugEnabled()) {
							LOG.debug("TM - taskService is " + task.getServiceCode());
						}
						TaskExecutionResult taskExecutionResult = executeTask(taskService, task, serviceResultBuilder);
						if (taskExecutionResult.stopAndRestart) {
							restart = true;
						}
						done = taskExecutionResult.done;
						errorMessage = taskExecutionResult.errorMessage;

					}
				} else {
					if (LOG.isDebugEnabled()) {
						LOG.debug("TM - taskService is null");
					}
				}

				if (done) {
					TasksLists tasksLists = setTaskDone(taskCluster, task);
					// Add new tasks to top of deque
					for (ITask iTask : tasksLists.newTasksToDos) {
						tasksQueue.addFirst(iTask);
					}

					for (ITask idTask : tasksLists.tasksToRemoves) {
						for (Iterator<ITask> iterator = recycleList.iterator(); iterator.hasNext();) {
							ITask iTask = iterator.next();
							if (idTask.equals(iTask)) {
								iterator.remove();
								break;
							}
						}
						for (Iterator<ITask> iterator = tasksQueue.iterator(); iterator.hasNext();) {
							ITask iTask = iterator.next();
							if (idTask.equals(iTask)) {
								iterator.remove();
								break;
							}
						}
					}

					if (taskService != null && !taskService.getNature().equals(ServiceNature.DATA_CHECK)) {
						// Add previously failed tasks to end of deque. Not done when service nature is not DATA_CHECK because DATA_CHECK does not update objects.
						for (ITask iTask : recycleList) {
							tasksQueue.addLast(iTask);
						}
						recycleList.clear();
					}
				} else {
					task = setTaskNothing(taskCluster, task, errorMessage);
					recycleList.add(task);
				}
				if (restart) {
					break;
				}
			}
			if (!restart && recycleList.isEmpty()) {
				getTaskManagerConfiguration().getTaskManagerWriter().archiveTaskCluster(taskCluster);
			}
		}

		serviceResultBuilder.ingest(restart());
		return serviceResultBuilder.compileResult(null);
	}

	private IServiceResult<Void> restart() {
		ServiceResultBuilder<TaskManagerErrorEnum> resultBuilder = new ServiceResultBuilder<TaskManagerErrorEnum>();
		// Serializable nextFromQueue = taskManagerServiceDelegate.getNextFromQueue();
		// if (nextFromQueue != null) {
		// resultBuilder.ingest(startEngine(nextFromQueue));
		// }
		return resultBuilder.compileResult(null);
	}

	private TaskExecutionResult executeTask(ITaskService taskService, ITask task, ServiceResultBuilder<TaskManagerErrorEnum> serviceResultBuilder) {
		TaskExecutionResult taskExecutionResult = new TaskExecutionResult();

		try {
			ITaskService.IExecutionResult executionResult = taskService.execute(task);
			if (executionResult == null) {
				throw new Exception("Task execution result is null");
			} else {

				if (executionResult.mustStopAndRestartTaskManager()) {
					taskExecutionResult.stopAndRestart = true;
					// if we have to stop and restart, the id cluster of the task might have been modified, we'd better reload it
					updateTaskWithResult(task, executionResult);
				} else {
					updateTaskWithResult(task, executionResult);
				}
				taskExecutionResult.done = executionResult.isFinished();
			}
		} catch (Throwable t) {
			serviceResultBuilder.addError(TaskManagerErrorEnum.TASK, "SERVICE_CODE", task.getServiceCode());
			LOG.error("TM - TaskCode = " + task.getServiceCode() + " - task = " + task, t);
			taskExecutionResult.errorMessage = t;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - " + task.getServiceCode() + (taskExecutionResult.done ? " - Success" : " - Failure"));
		}

		return taskExecutionResult;
	}

	private void updateTaskWithResult(ITask task, ITaskService.IExecutionResult executionResult) {
		task.setResultStatus(executionResult.getResultStatus());
		IStackResult stackResult = executionResult.getStackResult();
		task.setResultDesc(executionResult.getResultDesc());
		if (stackResult != null) {
			if (StringUtils.isBlank(task.getResultDesc())) { // if result desc is null, we use the one from the first stack
				task.setResultDesc(stackResult.getResultText());
			}
			ITaskDefinition taskDefinition = getTaskManagerConfiguration().getTaskDefinitionRegistry().getTaskDefinition(task.getServiceCode());
			if (taskDefinition.getResultDepth() > 0) {
				task.setResultDetail(getTaskManagerConfiguration().getTaskResultDetailBuilder().buildStack(stackResult, taskDefinition.getResultDepth()));
			}
		}
	}

	/*
	 * Set task Done and move others tasks
	 */
	private TasksLists setTaskDone(ITaskCluster taskCluster, ITask task) {
		return nextTasks(taskCluster, task, false);
	}

	/*
	 * Set task as nothing
	 */
	private ITask setTaskNothing(ITaskCluster taskCluster, ITask task, Throwable errorMessage) {
		return getTaskManagerConfiguration().getTaskManagerWriter().saveNothingTask(taskCluster, task);
	}

	private TasksLists nextTasks(ITaskCluster taskCluster, ITask toDoneTask, boolean skip) {
		List<ITask> todoTasks = getTaskManagerConfiguration().getTaskManagerReader().findNextTodoTasksByTaskClusterTask(taskCluster, toDoneTask);

		// if (task.getIdPreviousUpdateStatusTask() != null) {
		// // Cancel tasks of other branches
		// tasksLists.tasksToRemoves.addAll(deleteOtherChildPreviousUpdateStatusTasks(task));
		// }

		List<Pair<ITask, List<TaskNode>>> replaceTasks = new ArrayList<Pair<ITask, List<TaskNode>>>();
		List<ITask> toDoneTasks = new ArrayList<ITask>();
		List<ITask> toCurrentTasks = new ArrayList<ITask>();

		// Done current task
		toDoneTasks.add(toDoneTask);

		// Step current next todo task
		if (todoTasks != null && !todoTasks.isEmpty()) {
			for (ITask todoTask : todoTasks) {
				if (todoTask.isCheckGroup()) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("TM - Task is group");
					}

					// Replace group with actual tasks.
					ITaskObjectManager<?> objectTypeTaskFactory = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(todoTask.getTaskObjectClass());
					String taskChainCriteria = objectTypeTaskFactory.getTaskChainCriteria(todoTask);

					List<TaskNode> ctr = _createTasks(todoTask, taskChainCriteria);

					replaceTasks.add(Pair.of(todoTask, ctr));
				} else {
					toCurrentTasks.add(todoTask);
				}
			}
		}

		NextTasksInTaskClusterResult nextTasksInTaskClusterResult = getTaskManagerConfiguration().getTaskManagerWriter().saveNextTasksInTaskCluster(taskCluster, replaceTasks, toDoneTasks,
				toCurrentTasks);

		TasksLists tasksLists = new TasksLists();
		tasksLists.newTasksToDos = new ArrayList<ITask>();
		if (nextTasksInTaskClusterResult.getCurrentTasks() != null && !nextTasksInTaskClusterResult.getCurrentTasks().isEmpty()) {
			tasksLists.newTasksToDos.addAll(nextTasksInTaskClusterResult.getCurrentTasks());
		}

		tasksLists.tasksToRemoves = new ArrayList<ITask>();
		if (nextTasksInTaskClusterResult.getDeleteTasks() != null && !nextTasksInTaskClusterResult.getDeleteTasks().isEmpty()) {
			tasksLists.tasksToRemoves.addAll(nextTasksInTaskClusterResult.getDeleteTasks());
		}

		return tasksLists;
	}

	// Task creation

	@SuppressWarnings("unchecked")
	private <E extends Enum<E>, F extends ITaskObject<E>> ITaskCluster createTaskCluster(F taskObject) {
		ITaskCluster taskCluster = getTaskManagerConfiguration().getTaskFactory().newTaskCluster();

		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewTaskCluster(taskCluster);

		TaskNode taskNode = createTasks(taskCluster, taskObject);

		NewTaskObjectsInTaskClusterResult newTaskObjectsInTaskClusterResult = getTaskManagerConfiguration().getTaskManagerWriter().saveNewTaskObjectsInTaskCluster(taskCluster,
				Arrays.asList(Pair.<ITaskObject<?>, TaskNode> of(taskObject, taskNode)));

		return newTaskObjectsInTaskClusterResult.getTaskCluster();
	}

	/*
	 * Create status graph and tasks
	 */
	private <E extends Enum<E>, F extends ITaskObject<E>> TaskNode createTasks(ITaskCluster taskCluster, F taskObject) {
		ITaskObjectManager<F> taskObjectManager = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(taskObject);
		Class<F> taskObjectClass = taskObjectManager.getTaskObjectClass();

		List<IStatusGraph<E>> statusGraphs = getTaskManagerConfiguration().getStatusGraphsByTaskObjectType(taskObjectClass);

		// Create a first task, it does nothing
		ITask initTask = getTaskManagerConfiguration().getTaskFactory().newTask();
		initTask.setServiceCode(null);
		initTask.setTaskStatus(TaskStatus.CURRENT);
		initTask.setCheckGroup(false);
		initTask.setTaskObjectClass(taskObjectClass);

		return new TaskNode(initTask, createTasks(taskCluster, taskObjectClass, taskObject, statusGraphs, taskObject.getStatus(), null));
	}

	private <E extends Enum<E>, F extends ITaskObject<E>> List<TaskNode> createTasks(ITaskCluster taskCluster, Class<F> taskObjectClass, F taskObject, List<IStatusGraph<E>> statusGraphs,
			E currentStatus, TaskNode previousUpdateStatusTaskNode) {
		List<TaskNode> taskNodes = new ArrayList<ITaskManagerWriter.TaskNode>();

		List<IStatusGraph<E>> sgs = findStatusGraphs(statusGraphs, currentStatus);
		if (sgs != null && !sgs.isEmpty()) {
			for (IStatusGraph<E> sg : sgs) {
				// Create group task
				ITask groupTask = getTaskManagerConfiguration().getTaskFactory().newTask();
				groupTask.setServiceCode(null);
				groupTask.setTaskStatus(TaskStatus.TODO);
				groupTask.setTaskObjectClass(taskObjectClass);
				groupTask.setCheckGroup(true);
				groupTask.setPreviousStatus(currentStatus != null ? currentStatus.name() : null);
				groupTask.setNextStatus(sg.getNextStatus().name());

				// Create update status task
				ITask updateStatusTask = getTaskManagerConfiguration().getTaskFactory().newTask();
				updateStatusTask.setServiceCode(sg.getCodeTaskType());
				updateStatusTask.setTaskStatus(TaskStatus.TODO);
				updateStatusTask.setNextStatus(sg.getNextStatus().name());
				updateStatusTask.setCheckGroup(false);
				updateStatusTask.setTaskObjectClass(taskObjectClass);

				// Create Nodes
				TaskNode updateStatusTaskNode = new TaskNode(updateStatusTask, new ArrayList<TaskNode>());
				updateStatusTaskNode.getChildTaskNodes().addAll(createTasks(taskCluster, taskObjectClass, taskObject, statusGraphs, sg.getNextStatus(), updateStatusTaskNode));

				TaskNode groupTaskNode = new TaskNode(groupTask, Arrays.asList(updateStatusTaskNode));

				taskNodes.add(groupTaskNode);
			}
		}

		return taskNodes;
	}

	private <E extends Enum<E>> List<IStatusGraph<E>> findStatusGraphs(List<IStatusGraph<E>> statusGraphs, E currentStatus) {
		List<IStatusGraph<E>> res = new ArrayList<IStatusGraph<E>>();

		if (statusGraphs != null && !statusGraphs.isEmpty()) {
			for (IStatusGraph<E> statusGraph : statusGraphs) {
				if ((currentStatus == null && statusGraph.getCurrentStatus() == null)
						|| (currentStatus != null && statusGraph.getCurrentStatus() != null && statusGraph.getCurrentStatus().equals(currentStatus))) {
					res.add(statusGraph);
				}
			}
		}

		return res;
	}

	/*
	 * Create Task graphs for task cluster
	 */
	private ITaskCluster createTaskGraphs(ITaskCluster taskCluster) {
		List<ITaskObject<?>> taskObjects = getTaskManagerConfiguration().getTaskManagerReader().findTaskObjectsByTaskCluster(taskCluster);

		List<Pair<ITaskObject<?>, TaskNode>> taskObjectNodes = new ArrayList<Pair<ITaskObject<?>, TaskNode>>();
		if (taskObjects != null && !taskObjects.isEmpty()) {
			for (ITaskObject<?> taskObject : taskObjects) {
				taskObjectNodes.add(Pair.<ITaskObject<?>, TaskNode> of(taskObject, createTasks(taskCluster, taskObject)));
			}
		}

		NewTaskObjectsInTaskClusterResult newTaskObjectsInTaskClusterResult = getTaskManagerConfiguration().getTaskManagerWriter().saveNewTaskObjectsInTaskCluster(taskCluster, taskObjectNodes);

		return newTaskObjectsInTaskClusterResult.getTaskCluster();
	}

	/*
	 * Create a tasks for parent and task chain criteria
	 */
	private List<TaskNode> _createTasks(ITask parentTask, String taskChainCriteria) {
		if (taskChainCriteria != null && !taskChainCriteria.isEmpty()) {
			AbstractGraphNode graphNode = GraphCalcHelper.buildGraphRule(taskChainCriteria);
			return _createTasks(parentTask, graphNode);
		}
		return null;
	}

	private List<TaskNode> _createTasks(ITask parentTask, AbstractGraphNode node) {
		if (node != null) {
			if (node instanceof IdGraphNode) {
				IdGraphNode ign = (IdGraphNode) node;
				ITask task = getTaskManagerConfiguration().getTaskFactory().newTask();
				task.setTaskObjectClass(parentTask.getTaskObjectClass());
				task.setServiceCode(ign.getId());
				task.setTaskStatus(TaskStatus.TODO);
				task.setNextStatus(parentTask.getNextStatus());
				task.setCheckGroup(false);

				return Arrays.asList(new TaskNode(task, new ArrayList<ITaskManagerWriter.TaskNode>()));
			} else if (node instanceof ParallelGraphNode) {
				ParallelGraphNode pgn = (ParallelGraphNode) node;

				List<TaskNode> taskNodes = new ArrayList<TaskNode>();
				for (AbstractGraphNode subNode : pgn.getNodes()) {
					taskNodes.addAll(_createTasks(parentTask, subNode));
				}

				return taskNodes;
			} else if (node instanceof NextGraphNode) {
				NextGraphNode ngn = (NextGraphNode) node;

				List<TaskNode> firstCr = _createTasks(parentTask, ngn.getFirstNode());
				List<TaskNode> nextCr = _createTasks(parentTask, ngn.getNextNode());

				if (firstCr != null && nextCr != null) {
					for (TaskNode firstTask : firstCr) {
						for (TaskNode nextTask : nextCr) {
							firstTask.getChildTaskNodes().add(nextTask);
						}
					}
				}

				return firstCr;
			}
		}
		return null;
	}

	// Inner class

	private class TaskExecutionResult {

		public boolean done;

		public Throwable errorMessage;

		public boolean stopAndRestart;

	}

	public class TasksLists {

		public List<ITask> tasksToRemoves;

		public List<ITask> newTasksToDos;

	}
}
