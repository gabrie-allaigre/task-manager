package com.synaptix.taskmanager.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.LocalDateTime;

import com.synaptix.component.model.IServiceResult;
import com.synaptix.component.model.IStackResult;
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
import com.synaptix.taskmanager.model.domains.EnumErrorMessages;
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
				LOG.debug("TM - Nothing");
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
				String errorMessage = null;
				if (task.getServiceCode() != null) {
					ITaskDefinition taskDefinition = getTaskManagerConfiguration().getTaskDefinitionRegistry().getTaskDefinition(task.getServiceCode());
					ITaskService taskService = taskDefinition.getTaskService();
					if (taskService == null) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("taskService does not exist");
						}
						errorMessage = "Service code does not exist";
					} else {
						if (LOG.isDebugEnabled()) {
							LOG.debug("taskService is " + task.getServiceCode());
						}
						// TaskExecutionResult taskExecutionResult = executeTask(taskService, task, serviceResultBuilder);
						// if (taskExecutionResult.stopAndRestart) {
						// restart = true;
						// }
						// done = taskExecutionResult.done;
						// errorMessage = taskExecutionResult.errorMessage;

					}
				} else {
					if (LOG.isDebugEnabled()) {
						LOG.debug("taskService is null");
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

					if (task.getNature() != ServiceNature.DATA_CHECK) {
						// Add previously failed tasks to end of deque. Not done when service nature is not DATA_CHECK because DATA_CHECK does not update objects.
						for (ITask iTask : recycleList) {
							tasksQueue.addLast(iTask);
						}
						recycleList.clear();
					}
				} else {
					setTaskNothing(task, errorMessage);
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

	private void setTaskNothing(ITask task, String errorMessage) {
		if (task.isCheckError()
				&& ((errorMessage == null && task.getErrorMessage() == null) || (errorMessage != null && task.getErrorMessage() != null && errorMessage.equals(task.getErrorMessage())))) {
			return;
		}
		if (!task.isCheckError()) {
			task.setFirstErrorDate(new LocalDateTime());
		}

		task.setErrorMessage(StringUtils.substring(errorMessage, 0, 2000));
		if (errorMessage != null && !EnumErrorMessages.ERROR_MESSAGE_WAITING.getMessage().equals(errorMessage)) {
			task.setCheckError(true);
		}

		if (!EnumErrorMessages.ERROR_MESSAGE_WAITING.getMessage().equals(errorMessage) && !task.isCheckGroup()) {
			ITaskObjectManager<?> objectTypeTaskFactory = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(task.getTaskObjectClass());
			saveTodoExecutant(task, objectTypeTaskFactory);
			saveTodoManager(task, objectTypeTaskFactory);
		} else {
			deleteTodos(task);
			task.setCheckTodoExecutantCreated(false);
			task.setCheckTodoManagerCreated(false);
		}

		// getTaskManagerConfiguration().getTaskManagerWriter().saveTask(task);
	}

	private TaskExecutionResult executeTask(ITaskService taskService, ITask task, ServiceResultBuilder<TaskManagerErrorEnum> serviceResultBuilder) {
		TaskExecutionResult taskExecutionResult = new TaskExecutionResult();

		try {
			ITaskService.IExecutionResult executionResult = taskService.execute(task);
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - " + task.getServiceCode() + (executionResult != null && executionResult.isFinished() ? " - Success" : " - Failure"));
			}
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
				if (executionResult.isFinished()) {
					taskExecutionResult.done = true;
				} else if (StringUtils.isNotEmpty(executionResult.getErrorMessage())) {
					taskExecutionResult.errorMessage = executionResult.getErrorMessage();
				} else if (executionResult.getErrors() != null) {
					taskExecutionResult.errorMessage = EnumErrorMessages.DEFAULT_ERROR_MESSAGE_LIST.getMessage();
				} else {
					taskExecutionResult.errorMessage = null;
				}
				// saveErrors(task, executionResult.getErrors());
			}
		} catch (Throwable t) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - " + task.getServiceCode() + " - Error");
			}
			serviceResultBuilder.addError(TaskManagerErrorEnum.TASK, "SERVICE_CODE", task.getServiceCode());
			LOG.error(t.getMessage() + " - TM - TaskCode = " + task.getServiceCode() + " - task = " + task, t);
			taskExecutionResult.errorMessage = ExceptionUtils.getRootCauseMessage(t);
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

		// getTaskManagerConfiguration().getTaskManagerWriter().saveTask(task);
	}

	private TasksLists setTaskDone(ITaskCluster taskCluster, ITask task) {
		return nextTasks(taskCluster, task, false);
	}

	public TasksLists nextTasks(ITaskCluster taskCluster, ITask toDoneTask, boolean skip) {
		List<ITask> todoTasks = getTaskManagerConfiguration().getTaskManagerReader().findNextTodoTasksByTaskClusterTask(taskCluster, toDoneTask);
		//
		// List<ITask> changedStatusTasks = new ArrayList<ITask>();
		// List<ITask> toCurrentTasks = new ArrayList<ITask>();
		//
		// if (task.getIdPreviousUpdateStatusTask() != null) {
		// // Cancel tasks of other branches
		// tasksLists.tasksToRemoves.addAll(deleteOtherChildPreviousUpdateStatusTasks(task));
		// }
		//
		// if (skip) {
		// saveTaskSkipped(task);
		// } else {
		// saveTaskDone(task);
		// }
		// changedStatusTasks.add(task);
		//

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
						LOG.debug("Task is group : explain");
					}

					// Replace group with actual tasks.
					ITaskObjectManager<?> objectTypeTaskFactory = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(todoTask.getTaskObjectClass());
					String taskChainCriteria = objectTypeTaskFactory.getTaskChainCriteria(todoTask);

					// CreateTasksResult ctr = _createTasks(todoTask, taskChainCriteria);
					// if (ctr != null) {
					// if (ctr.allTasks != null) {
					// changedStatusTasks.addAll(ctr.allTasks);
					// }
					//
					// if (ctr.firstTasks != null) {
					// toCurrentTasks.addAll(ctr.firstTasks);
					// tasksLists.getNewTasksToDo().addAll(ctr.firstTasks);
					//
					// for (ITask iTask : ctr.firstTasks) {
					// List<Serializable> previousTasks = getTaskMapper().findPreviousTasks(todoTask.getId());
					// for (Serializable idPreviousTask : previousTasks) {
					// linkTwoTasks(idPreviousTask, iTask.getId());
					// }
					// }
					// }
					//
					// for (ITask iTask : ctr.lastTasks) {
					// List<Serializable> nextTasks = getTaskMapper().findNextTasks(todoTask.getId());
					// for (Serializable idNextTask : nextTasks) {
					// linkTwoTasks(iTask.getId(), idNextTask);
					// }
					// }
					// } else {
					// List<Serializable> previousTasks = getTaskMapper().findPreviousTasks(todoTask.getId());
					// List<ITask> nextTasks = getTaskMapper().selectNextTasks(todoTask.getId(), null);
					// if (previousTasks != null && !previousTasks.isEmpty() && nextTasks != null && !nextTasks.isEmpty()) {
					// linkTwoTasks(previousTasks.get(0), nextTasks.get(0).getId());
					// }
					// if (nextTasks != null) {
					// toCurrentTasks.addAll(nextTasks);
					// tasksLists.getNewTasksToDo().addAll(nextTasks);
					// }
					// }
					// deleteTask(todoTask.getId());
					// tasksLists.getIdTasksToRemove().add(todoTask.getId());
				} else {
					toCurrentTasks.add(todoTask);
					// saveTaskCurrent(todoTask);
					// changedStatusTasks.add(todoTask);
					// tasksLists.getNewTasksToDo().add(todoTask);
				}
			}
		}
		//
		// onTaskStatusChanged(changedStatusTasks);
		//
		// if (!toCurrentTasks.isEmpty()) {
		// for (ITask t : toCurrentTasks) {
		// saveTaskCurrent(t);
		// }
		// }
		//
		// onTaskStatusChanged(toCurrentTasks);

		NextTasksInTaskClusterResult nextTasksInTaskClusterResult = getTaskManagerConfiguration().getTaskManagerWriter().saveNextTasksInTaskCluster(taskCluster, replaceTasks, toDoneTasks,
				toCurrentTasks);

		// onTaskStatusChanged(nextTasksInTaskClusterResult.getTasks());

		TasksLists tasksLists = new TasksLists();
		tasksLists.newTasksToDos = new ArrayList<ITask>();
		if (nextTasksInTaskClusterResult.getTasks() != null && !nextTasksInTaskClusterResult.getTasks().isEmpty()) {
			for (ITask task : nextTasksInTaskClusterResult.getTasks()) {
				if (TaskStatus.CURRENT.equals(task.getTaskStatus())) {
					tasksLists.newTasksToDos.add(task);
				}
			}
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

		onTaskStatusChanged(newTaskObjectsInTaskClusterResult.getTasks());

		return newTaskObjectsInTaskClusterResult.getTaskCluster();
	}

	/*
	 * Create status graph and tasks
	 */
	private <E extends Enum<E>, F extends ITaskObject<E>> TaskNode createTasks(ITaskCluster taskCluster, F taskObject) {
		E currentStatus = null;

		ITaskObjectManager<F> taskObjectManager = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(taskObject);
		Class<F> taskObjectClass = taskObjectManager.getTaskObjectClass();

		List<IStatusGraph<E>> statusGraphs = getTaskManagerConfiguration().getStatusGraphsByTaskObjectType(taskObjectClass);

		E firstStatus = null;
		for (IStatusGraph<E> statusGraph : statusGraphs) {
			if (statusGraph.getCurrentStatus() == null) {
				firstStatus = statusGraph.getNextStatus();
			}
		}

		// Create a first task, it does nothing
		ITask initTask = getTaskManagerConfiguration().getTaskFactory().newTask(taskCluster, taskObject);
		initTask.setCheckError(false);
		initTask.setCheckSkippable(false);
		initTask.setErrorMessage(null);
		initTask.setExecutantRole(null);
		initTask.setManagerRole(null);
		initTask.setNature(null);
		initTask.setServiceCode(null);
		initTask.setTaskStatus(TaskStatus.CURRENT);
		initTask.setStartDate(new Date());
		initTask.setNextStatus(firstStatus.name());
		initTask.setCheckGroup(false);
		initTask.setCheckTodoExecutantCreated(false);
		initTask.setCheckTodoManagerCreated(false);
		initTask.setFirstErrorDate(null);
		initTask.setTodoManagerDuration(null);
		initTask.setTaskObjectClass(taskObjectClass);

		return new TaskNode(initTask, createTasks(taskCluster, taskObjectClass, taskObject, statusGraphs, currentStatus));
	}

	private <E extends Enum<E>, F extends ITaskObject<E>> List<TaskNode> createTasks(ITaskCluster taskCluster, Class<F> taskObjectClass, F taskObject, List<IStatusGraph<E>> statusGraphs,
			E currentStatus) {
		List<TaskNode> taskNodes = new ArrayList<ITaskManagerWriter.TaskNode>();

		List<IStatusGraph<E>> sgs = findStatusGraphs(statusGraphs, currentStatus);
		if (sgs != null && !sgs.isEmpty()) {
			for (IStatusGraph<E> sg : sgs) {
				// Create group task
				ITask groupTask = getTaskManagerConfiguration().getTaskFactory().newTask(taskCluster, taskObject);
				groupTask.setCheckError(false);
				groupTask.setCheckSkippable(false);
				groupTask.setErrorMessage(null);
				groupTask.setExecutantRole(null);
				groupTask.setManagerRole(null);
				groupTask.setNature(null);
				groupTask.setServiceCode(null);
				groupTask.setTaskStatus(TaskStatus.TODO);
				groupTask.setNextStatus(sg.getNextStatus().name());
				groupTask.setCheckGroup(true);
				groupTask.setCheckTodoExecutantCreated(false);
				groupTask.setCheckTodoManagerCreated(false);
				groupTask.setFirstErrorDate(null);
				groupTask.setTodoManagerDuration(null);
				groupTask.setTaskObjectClass(taskObjectClass);

				// Create update status task
				ITaskDefinition updateStatusTaskType = getTaskManagerConfiguration().getTaskDefinitionRegistry().getTaskDefinition(sg.getCodeTaskType());
				ITaskService taskService = updateStatusTaskType.getTaskService();

				ITask updateStatusTask = getTaskManagerConfiguration().getTaskFactory().newTask(taskCluster, taskObject);
				updateStatusTask.setCheckError(false);
				updateStatusTask.setCheckSkippable(updateStatusTaskType.isCheckSkippable());
				updateStatusTask.setErrorMessage(null);
				updateStatusTask.setExecutantRole(updateStatusTaskType.getExecutantRole());
				updateStatusTask.setManagerRole(updateStatusTaskType.getManagerRole());
				updateStatusTask.setNature(taskService.getNature());
				updateStatusTask.setServiceCode(updateStatusTaskType.getCode());
				updateStatusTask.setTaskStatus(TaskStatus.TODO);
				updateStatusTask.setNextStatus(sg.getNextStatus().name());
				updateStatusTask.setCheckGroup(false);
				updateStatusTask.setCheckTodoExecutantCreated(false);
				updateStatusTask.setCheckTodoManagerCreated(false);
				updateStatusTask.setFirstErrorDate(null);
				updateStatusTask.setTodoManagerDuration(updateStatusTaskType.getTodoManagerDuration());
				updateStatusTask.setTaskObjectClass(taskObjectClass);

				// Create Nodes
				TaskNode updateStatusTaskNode = new TaskNode(updateStatusTask, createTasks(taskCluster, taskObjectClass, taskObject, statusGraphs, sg.getNextStatus()));

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

	/**
	 * Create Task graphs for task cluster
	 * 
	 * @param taskCluster
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

		onTaskStatusChanged(newTaskObjectsInTaskClusterResult.getTasks());

		return newTaskObjectsInTaskClusterResult.getTaskCluster();
	}

	private void onTaskStatusChanged(List<ITask> tasks) {
		// TODO
	}

	// todo list

	/*
	 * Create and save a todo for the manager. <br> This method must be called inside a dao session.
	 */
	private void saveTodoManager(ITask task, ITaskObjectManager<?> objectTypeTaskFactory) {
		// if (task.isCheckGroup() || task.isCheckTodoManagerCreated() || objectTypeTaskFactory == null) {
		// return;
		// }
		//
		// IEntity ownerEntity = objectTypeTaskFactory.getManager(task);
		// if (ownerEntity == null) {
		// return;
		// }
		// IEntity contactEntity = objectTypeTaskFactory.getExecutant(task);
		//
		// ITodo todo = createTodo(task, TodoOwner.MANAGER, objectTypeTaskFactory, ownerEntity, contactEntity);
		//
		// saveEntity(todo);
		//
		// task.setCheckTodoManagerCreated(true);
		// saveOrUpdateEntity(task);
	}

	/*
	 * Create and save todo executant
	 */
	private void saveTodoExecutant(ITask task, ITaskObjectManager<?> objectTypeTaskFactory) {
		// if (task.isCheckGroup() || task.isCheckTodoExecutantCreated() || objectTypeTaskFactory == null) {
		// return;
		// }
		//
		// IEntity ownerEntity = objectTypeTaskFactory.getExecutant(task);
		// if (ownerEntity == null) {
		// return;
		// }
		//
		// IEntity contactEntity = objectTypeTaskFactory.getManager(task);
		//
		// ITodo todo = createTodo(task, TodoOwner.EXECUTANT, objectTypeTaskFactory, ownerEntity, contactEntity);
		//
		// saveEntity(todo);
		//
		// task.setCheckTodoExecutantCreated(true);
		// saveOrUpdateEntity(task);

	}

	/*
	 * Delete all todo to task
	 */
	private void deleteTodos(ITask task) {
		if (task.isCheckTodoExecutantCreated() || task.isCheckTodoManagerCreated()) {
			// getTaskManagerConfiguration().getTaskManagerWriter().deleteTasksTodo(task);
		}
	}

	// Inner class

	private class TaskExecutionResult {

		public boolean done;

		public String errorMessage;

		public boolean stopAndRestart;

	}

	public class TasksLists {

		public List<ITask> tasksToRemoves;

		public List<ITask> newTasksToDos;

	}
}
