package com.synaptix.taskmanager.engine;

import com.synaptix.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.synaptix.taskmanager.engine.configuration.transform.ITaskChainCriteriaTransform;
import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.listener.ITaskCycleListener;
import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.synaptix.taskmanager.engine.taskservice.ITaskService;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.event.EventListenerList;
import java.util.*;
import java.util.Map.Entry;

public class TaskManagerEngine {

	private static final Log LOG = LogFactory.getLog(TaskManagerEngine.class);

	private ITaskManagerConfiguration taskManagerConfiguration;

	private EventListenerList eventListenerList;

	public TaskManagerEngine(ITaskManagerConfiguration taskManagerConfiguration) {
		super();

		this.taskManagerConfiguration = taskManagerConfiguration;

		this.eventListenerList = new EventListenerList();
	}

	public ITaskManagerConfiguration getTaskManagerConfiguration() {
		return taskManagerConfiguration;
	}

	// Listener

	public void addTaskManagerListener(ITaskCycleListener taskManagerListener) {
		eventListenerList.add(ITaskCycleListener.class, taskManagerListener);
	}

	public void removeTaskManagerListener(ITaskCycleListener taskManagerListener) {
		eventListenerList.remove(ITaskCycleListener.class, taskManagerListener);
	}

	public ITaskCycleListener[] getTaskCycleListeners() {
		return eventListenerList.getListeners(ITaskCycleListener.class);
	}

	// Start engine

	/**
	 * @param taskObjects
	 * @return
	 */
	public ITaskCluster startEngine(ITaskObject... taskObjects) {
		if (taskObjects == null || taskObjects.length == 0) {
			return null;
		}

		Set<ITaskCluster> taskClusters = new HashSet<ITaskCluster>();
		Set<ITaskObject> createClusters = new HashSet<ITaskObject>();
		for (ITaskObject taskObject : taskObjects) {
			if (taskObject != null) {
				// If cluster not existe, create
				ITaskCluster taskCluster = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
				if (taskCluster == null) {
					createClusters.add(taskObject);
				} else {
					taskClusters.add(taskCluster);
				}
			}
		}

		ITaskCluster res = null;
		if (!createClusters.isEmpty()) {
			res = createTaskCluster(createClusters.toArray(new ITaskObject[createClusters.size()]));
			taskClusters.add(res);
		} else if (taskClusters.size() == 1) {
			res = taskClusters.iterator().next();
		}

		startEngine(taskClusters.toArray(new ITaskCluster[taskClusters.size()]));

		return res;
	}

	/**
	 * Start engin for taskCluster
	 *
	 * @param taskClusters
	 * @return
	 */
	public void startEngine(ITaskCluster... taskClusters) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - StartEngine");
		}

		if (taskClusters == null || taskClusters.length == 0) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - Nothing, cluster is null or empty");
			}
			return;
		}

		LinkedList<ITaskCluster> restartClusters = new LinkedList<ITaskCluster>();
		for (ITaskCluster taskCluster : taskClusters) {
			if (taskCluster != null && !taskCluster.isCheckArchived() && !restartClusters.contains(taskCluster)) {
				restartClusters.add(taskCluster);
			}
		}
		while (!restartClusters.isEmpty()) {
			ITaskCluster taskCluster = restartClusters.removeFirst();

			boolean restart = false;

			// Find all current task for cluster
			List<? extends ICommonTask> tasks = getTaskManagerConfiguration().getTaskManagerReader().findCurrentTasksByTaskCluster(taskCluster);

			if (tasks == null || tasks.isEmpty()) {
				if (taskCluster != null && !taskCluster.isCheckGraphCreated()) {
					createTaskGraphsForTaskCluster(taskCluster);
					restartClusters.add(taskCluster);
				} else {
					getTaskManagerConfiguration().getTaskManagerWriter().archiveTaskCluster(taskCluster);
				}
			} else {
				LinkedList<ICommonTask> tasksQueue = new LinkedList<ICommonTask>(tasks);
				List<ICommonTask> recycleList = new ArrayList<ICommonTask>();

				while (!tasksQueue.isEmpty()) {
					ICommonTask task = tasksQueue.removeFirst();

					boolean done = true;
					Throwable errorMessage = null;
					ITaskService taskService = null;
					Object taskServiceResult = null;
					boolean noChanges = false;
					if (task.getCodeTaskDefinition() != null) {
						ITaskDefinition taskDefinition = getTaskManagerConfiguration().getTaskDefinitionRegistry().getTaskDefinition(task.getCodeTaskDefinition());
						if (taskDefinition == null || taskDefinition.getTaskService() == null) {
							if (LOG.isDebugEnabled()) {
								LOG.debug("TM - TaskService does not exist code=" + task.getCodeTaskDefinition());
							}
							errorMessage = new NotFoundTaskDefinitionException();
						} else {
							if (LOG.isDebugEnabled()) {
								LOG.debug("TM - Execute taskService code=" + task.getCodeTaskDefinition());
							}
							taskService = getTaskService(task);
							try {
								MyEngineContext context = new MyEngineContext(taskCluster, taskDefinition);

								ITaskService.IExecutionResult executionResult = taskService.execute(context, task);
								if (executionResult == null) {
									throw new NullTaskExecutionException();
								} else {
									done = executionResult.isFinished();
									taskServiceResult = executionResult.getResult();
									noChanges = executionResult.isNoChanges();
									if (executionResult.mustStopAndRestartTaskManager()) {
										restart = true;
										restartClusters.add(taskCluster);
									}
									if (done) {
										executeContext(context);
									}
								}
							} catch (Throwable t) {
								LOG.error("TM - Error taskService code=" + task.getCodeTaskDefinition(), t);
								errorMessage = t;
								done = false;
							}

							if (LOG.isDebugEnabled()) {
								LOG.debug("TM - Finish code=" + task.getCodeTaskDefinition() + (done ? " - Success" : " - Failure"));
							}
						}
					} else {
						if (LOG.isDebugEnabled()) {
							LOG.debug("TM - TaskService is null");
						}
					}

					if (done) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("TM - Task is done");
						}

						TasksLists tasksLists = setTaskDone(taskCluster, task, taskServiceResult);
						// Add new tasks to top of deque
						if (tasksLists.newCurrentTasks != null && !tasksLists.newCurrentTasks.isEmpty()) {
							for (ICommonTask iTask : tasksLists.newCurrentTasks) {
								tasksQueue.addFirst(iTask);
							}
						}

						if (tasksLists.tasksToRemoves != null && !tasksLists.tasksToRemoves.isEmpty()) {
							for (ICommonTask idTask : tasksLists.tasksToRemoves) {
								for (Iterator<ICommonTask> iterator = recycleList.iterator(); iterator.hasNext(); ) {
									ICommonTask iTask = iterator.next();
									if (idTask.equals(iTask)) {
										iterator.remove();
										break;
									}
								}
								for (Iterator<ICommonTask> iterator = tasksQueue.iterator(); iterator.hasNext(); ) {
									ICommonTask iTask = iterator.next();
									if (idTask.equals(iTask)) {
										iterator.remove();
										break;
									}
								}
							}
						}

						if (!noChanges) {
							// Add previously failed tasks to end of deque. Not done when service nature is not DATA_CHECK because DATA_CHECK does not update objects.
							for (ICommonTask iTask : recycleList) {
								tasksQueue.addLast(iTask);
							}
							recycleList.clear();
						}
					} else {
						if (LOG.isDebugEnabled()) {
							LOG.debug("TM - Task did nothing");
						}

						setTaskNothing(taskCluster, task, taskServiceResult, errorMessage);

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
		}
	}

	/**
	 * Add task objects in task cluster and startEngine
	 *
	 * @param taskCluster
	 * @param taskObjects
	 */
	public void addTaskObjectsToTaskCluster(ITaskCluster taskCluster, ITaskObject... taskObjects) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - addTaskObjectsToTaskCluster");
		}

		if (taskCluster == null || taskCluster.isCheckArchived()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - Nothing, cluster is null or archived");
			}
			return;
		}
		if (taskObjects == null || taskObjects.length == 0) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - Nothing, taskObject is null");
			}
			return;
		}

		List<ITaskObject> adds = new ArrayList<ITaskObject>();
		for (ITaskObject taskObject : taskObjects) {
			if (taskObject != null) {
				ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
				if (tc != null) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("TM - Nothing, taskObject is other or same in cluster");
					}
				} else {
					adds.add(taskObject);
				}
			}
		}

		createTaskGraphForTaskCluster(taskCluster, adds.toArray(new ITaskObject[adds.size()]));
		startEngine(taskCluster);
	}

	/**
	 * Remove task objects in task cluster and startEngine
	 *
	 * @param taskObjects
	 */
	public void removeTaskObjectsFromTaskCluster(ITaskObject... taskObjects) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - removeTaskObjectsFromTaskCluster");
		}

		if (taskObjects == null || taskObjects.length == 0) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - Nothing, taskObject is null");
			}
			return;
		}

		Map<ITaskCluster, List<ITaskObject>> modifyClusterMap = new HashMap<ITaskCluster, List<ITaskObject>>();
		for (ITaskObject taskObject : taskObjects) {
			if (taskObject != null) {
				ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
				if (tc != null) {
					List<ITaskObject> tos = modifyClusterMap.get(tc);
					if (tos == null) {
						tos = new ArrayList<ITaskObject>();
						modifyClusterMap.put(tc, tos);
					}
					if (!tos.contains(taskObject)) {
						tos.add(taskObject);
					}
				}
			}
		}

		for (Entry<ITaskCluster, List<ITaskObject>> entry : modifyClusterMap.entrySet()) {
			getTaskManagerConfiguration().getTaskManagerWriter().saveRemoveTaskObjectsFromTaskCluster(entry.getKey(), entry.getValue());
		}

		startEngine(modifyClusterMap.keySet().toArray(new ITaskCluster[modifyClusterMap.size()]));
	}

	/**
	 * Move task objects (with other cluster) to new task cluster, start engin on all cluster
	 *
	 * @param taskObjects
	 * @return
	 */
	public ITaskCluster moveTaskObjectsToNewTaskCluster(ITaskObject... taskObjects) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - moveTaskObjectsToNewTaskCluster");
		}

		if (taskObjects == null || taskObjects.length == 0) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - Nothing, taskObject is null");
			}
			return null;
		}

		Map<ITaskCluster, List<ITaskObject>> modifyClusterMap = new HashMap<ITaskCluster, List<ITaskObject>>();
		for (ITaskObject taskObject : taskObjects) {
			if (taskObject != null) {
				ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
				if (tc != null) {
					List<ITaskObject> tos = modifyClusterMap.get(tc);
					if (tos == null) {
						tos = new ArrayList<ITaskObject>();
						modifyClusterMap.put(tc, tos);
					}
					if (!tos.contains(taskObject)) {
						tos.add(taskObject);
					}
				}
			}
		}

		ITaskCluster taskCluster = null;
		if (!modifyClusterMap.isEmpty()) {
			taskCluster = getTaskManagerConfiguration().getTaskFactory().newTaskCluster();
			taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewTaskCluster(taskCluster);

			taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveMoveTaskObjectsToTaskCluster(taskCluster, modifyClusterMap, true);

			List<ITaskCluster> cs = new ArrayList<ITaskCluster>(modifyClusterMap.keySet());
			cs.add(taskCluster);
			startEngine(cs.toArray(new ITaskCluster[cs.size()]));
		}
		return taskCluster;
	}

	/**
	 * Move task objects (with other cluster) to task cluster, start engin on all cluster
	 *
	 * @param dstTaskCluster
	 * @param taskObjects
	 */
	public void moveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, ITaskObject... taskObjects) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - addTaskObjectsToTaskCluster");
		}

		if (dstTaskCluster == null || dstTaskCluster.isCheckArchived()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - Nothing, cluster is null or archived");
			}
			return;
		}
		if (taskObjects == null || taskObjects.length == 0) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - Nothing, taskObject is null");
			}
			return;
		}

		Map<ITaskCluster, List<ITaskObject>> modifyClusterMap = new HashMap<ITaskCluster, List<ITaskObject>>();
		for (ITaskObject taskObject : taskObjects) {
			if (taskObject != null) {
				ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
				if (tc != null && !tc.equals(dstTaskCluster)) {
					List<ITaskObject> tos = modifyClusterMap.get(tc);
					if (tos == null) {
						tos = new ArrayList<ITaskObject>();
						modifyClusterMap.put(tc, tos);
					}
					if (!tos.contains(taskObject)) {
						tos.add(taskObject);
					}
				}
			}
		}

		if (!modifyClusterMap.isEmpty()) {
			getTaskManagerConfiguration().getTaskManagerWriter().saveMoveTaskObjectsToTaskCluster(dstTaskCluster, modifyClusterMap, false);

			List<ITaskCluster> cs = new ArrayList<ITaskCluster>(modifyClusterMap.keySet());
			cs.add(dstTaskCluster);
			startEngine(cs.toArray(new ITaskCluster[cs.size()]));
		}
	}

	// Private Methods

	private void executeContext(MyEngineContext context) {
		context.lock = true;
	}

	/*
	 * Set task as nothing
	 */
	private void setTaskNothing(ITaskCluster taskCluster, ICommonTask task, Object taskServiceResult, Throwable throwable) {
		getTaskManagerConfiguration().getTaskManagerWriter().saveNothingTask(taskCluster, task, taskServiceResult, throwable);

		onNothingTasks(Arrays.<ICommonTask>asList(task));
	}

	/*
	 * Set task Done and move others tasks
	 */
	private TasksLists setTaskDone(ITaskCluster taskCluster, ICommonTask task, Object taskServiceResult) {
		return nextTasks(taskCluster, task, taskServiceResult, false);
	}

	@SuppressWarnings("unchecked")
	private TasksLists nextTasks(ITaskCluster taskCluster, ICommonTask toDoneTask, Object taskServiceResult, boolean skip) {
		TasksLists tasksLists = new TasksLists();

		List<ICommonTask> nextTodoTasks = new ArrayList<ICommonTask>();
		List<ICommonTask> nextCurrentTasks = new ArrayList<ICommonTask>();
		List<ICommonTask> toDeleteTasks = new ArrayList<ICommonTask>();
		if (getTaskManagerConfiguration().getTaskFactory().isStatusTask(toDoneTask)) {
			IStatusTask statusTask = (IStatusTask) toDoneTask;

			Class<? extends ITaskObject> taskObjectClass = (Class<? extends ITaskObject>) statusTask.getTaskObjectClass();

			List<? extends ICommonTask> oldOtherPreviousNextTasks = getTaskManagerConfiguration().getTaskManagerReader().findOtherBranchFirstTasksByStatusTask(statusTask);

			if (oldOtherPreviousNextTasks != null && !oldOtherPreviousNextTasks.isEmpty()) {
				toDeleteTasks.addAll(extractAllTasks(oldOtherPreviousNextTasks));
			}

			ITaskObjectManager<Object, ? extends ITaskObject> taskObjectManager = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(taskObjectClass);
			List<IStatusGraph<Object>> statusGraphs = taskObjectManager.getNextStatusGraphsByTaskObjectType(statusTask, statusTask.getCurrentStatus());

			List<ICommonTask> newTasks = new ArrayList<ICommonTask>();
			Map<ISubTask, List<ICommonTask>> linkNextTasksMap = new HashMap<ISubTask, List<ICommonTask>>();

			Map<IStatusTask, List<ICommonTask>> otherBranchFirstTasksMap = new HashMap<IStatusTask, List<ICommonTask>>();

			if (statusGraphs != null && !statusGraphs.isEmpty()) {
				List<IStatusTask> nextstatusTasks = new ArrayList<IStatusTask>();
				Map<Object, List<? extends ICommonTask>> map = new HashMap<Object, List<? extends ICommonTask>>();

				for (IStatusGraph<Object> statusGraph : statusGraphs) {
					// Create sub tasks
					String taskChainCriteria = taskObjectManager.getTaskChainCriteria(statusTask, statusGraph.getPreviousStatus(), statusGraph.getCurrentStatus());
					ITaskChainCriteriaTransform.IResult result = getTaskManagerConfiguration().getTaskChainCriteriaBuilder().transformeToTasks(getTaskManagerConfiguration(), taskChainCriteria);

					IStatusTask nextstatusTask = getTaskManagerConfiguration().getTaskFactory().newStatusTask(statusGraph.getStatusTaskServiceCode(), taskObjectClass, statusGraph.getCurrentStatus());

					newTasks.add(nextstatusTask);

					if (result != null && result.getNewSubTasks() != null && !result.getNewSubTasks().isEmpty()) {
						for (ISubTask newSubTask : result.getNewSubTasks()) {
							List<ISubTask> nextTasks = result.getLinkNextTasksMap().get(newSubTask);
							if (nextTasks != null && !nextTasks.isEmpty()) {
								linkNextTasksMap.put(newSubTask, new ArrayList<ICommonTask>(nextTasks));
							} else {
								linkNextTasksMap.put(newSubTask, Arrays.<ICommonTask>asList(nextstatusTask));
							}
						}

						newTasks.addAll(result.getNewSubTasks());

						nextTodoTasks.addAll(result.getNewSubTasks());
						nextCurrentTasks.addAll(result.getNextSubTasks());

						map.put(statusGraph.getCurrentStatus(), result.getNextSubTasks());
					} else {
						nextTodoTasks.add(nextstatusTask);
						nextCurrentTasks.add(nextstatusTask);

						map.put(statusGraph.getCurrentStatus(), Arrays.asList(nextstatusTask));
					}

					nextstatusTasks.add(nextstatusTask);
				}

				for (IStatusTask task : nextstatusTasks) {
					List<ICommonTask> otherBranchFirstTasks = new ArrayList<ICommonTask>();
					for (Entry<Object, List<? extends ICommonTask>> entry : map.entrySet()) {
						if (!entry.getKey().equals(task.getCurrentStatus())) {
							otherBranchFirstTasks.addAll(entry.getValue());
						}
					}
					otherBranchFirstTasksMap.put(task, otherBranchFirstTasks);
				}
			}

			getTaskManagerConfiguration().getTaskManagerWriter()
					.saveNewNextTasksInTaskCluster(taskCluster, statusTask, taskServiceResult, newTasks, linkNextTasksMap, otherBranchFirstTasksMap, nextCurrentTasks, toDeleteTasks);
		} else if (getTaskManagerConfiguration().getTaskFactory().isSubTask(toDoneTask)) {
			List<? extends ICommonTask> nextTasks = getTaskManagerConfiguration().getTaskManagerReader().findNextTasksBySubTask((ISubTask) toDoneTask);

			if (nextTasks != null && !nextTasks.isEmpty()) {
				nextCurrentTasks.addAll(nextTasks);
			}

			getTaskManagerConfiguration().getTaskManagerWriter().saveNextTasksInTaskCluster(taskCluster, toDoneTask, taskServiceResult, nextCurrentTasks);
		}

		onDoneTasks(Arrays.asList(toDoneTask));
		onTodoTasks(nextTodoTasks);
		onCurrentTasks(nextCurrentTasks);
		onDeleteTasks(toDeleteTasks);

		tasksLists.newCurrentTasks = nextCurrentTasks;
		tasksLists.tasksToRemoves = toDeleteTasks;

		return tasksLists;
	}

	/*
	 * Extract all tasks
	 */
	private List<? extends ICommonTask> extractAllTasks(List<? extends ICommonTask> tasks) {
		List<ICommonTask> res = new ArrayList<ICommonTask>();
		if (tasks != null && !tasks.isEmpty()) {
			for (ICommonTask task : tasks) {
				res.add(task);
				if (task instanceof ISubTask) {
					ISubTask subTask = (ISubTask) task;
					List<? extends ICommonTask> nextTasks = getTaskManagerConfiguration().getTaskManagerReader().findNextTasksBySubTask(subTask);
					res.addAll(extractAllTasks(nextTasks));
				}
			}
		}
		return res;
	}

	// Task creation

	/*
	 * Create task cluster for taskobject, create new task
	 */
	private ITaskCluster createTaskCluster(ITaskObject... taskObjects) {
		ITaskCluster taskCluster = getTaskManagerConfiguration().getTaskFactory().newTaskCluster();
		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewTaskCluster(taskCluster);

		return createTaskGraphForTaskCluster(taskCluster, taskObjects);
	}

	/*
	 * Create status graph and tasks
	 */
	private <E extends Object, F extends ITaskObject> IStatusTask createInitTask(F taskObject) {
		ITaskObjectManager<E, F> taskObjectManager = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(taskObject);
		Class<F> taskObjectClass = taskObjectManager.getTaskObjectClass();

		E currentStatus = taskObjectManager.getInitialStatus(taskObject);

		// Create a first task, it does nothing
		IStatusTask initTask = getTaskManagerConfiguration().getTaskFactory().newStatusTask(null, taskObjectClass, currentStatus);

		return initTask;
	}

	/*
	 * Create Task graphs for task cluster
	 */
	private ITaskCluster createTaskGraphsForTaskCluster(ITaskCluster taskCluster) {
		List<? extends ITaskObject> taskObjects = getTaskManagerConfiguration().getTaskManagerReader().findTaskObjectsByTaskCluster(taskCluster);

		List<IStatusTask> statusTasks = null;
		List<Pair<ITaskObject, IStatusTask>> taskObjectNodes = new ArrayList<Pair<ITaskObject, IStatusTask>>();
		if (taskObjects != null && !taskObjects.isEmpty()) {
			statusTasks = new ArrayList<IStatusTask>();
			for (ITaskObject taskObject : taskObjects) {
				IStatusTask initTask = createInitTask(taskObject);
				taskObjectNodes.add(Pair.<ITaskObject, IStatusTask>of(taskObject, initTask));
			}
		}

		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewGraphFromTaskCluster(taskCluster, taskObjectNodes);

		onTodoTasks(statusTasks);
		onCurrentTasks(statusTasks);

		return taskCluster;
	}

	/*
	 * Create Task graph for task object and add in task cluster
	 */
	private ITaskCluster createTaskGraphForTaskCluster(ITaskCluster taskCluster, ITaskObject... taskObjects) {
		List<IStatusTask> statusTasks = null;
		List<Pair<ITaskObject, IStatusTask>> taskObjectNodes = new ArrayList<Pair<ITaskObject, IStatusTask>>();
		if (taskObjects != null && taskObjects.length > 0) {
			statusTasks = new ArrayList<IStatusTask>();
			for (ITaskObject taskObject : taskObjects) {
				IStatusTask initTask = createInitTask(taskObject);
				taskObjectNodes.add(Pair.<ITaskObject, IStatusTask>of(taskObject, initTask));
			}
		}

		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewGraphFromTaskCluster(taskCluster, taskObjectNodes);

		onTodoTasks(statusTasks);
		onCurrentTasks(statusTasks);

		return taskCluster;
	}

	private ITaskService getTaskService(ICommonTask task) {
		if (task.getCodeTaskDefinition() != null) {
			ITaskDefinition taskDefinition = getTaskManagerConfiguration().getTaskDefinitionRegistry().getTaskDefinition(task.getCodeTaskDefinition());
			if (taskDefinition != null) {
				return taskDefinition.getTaskService();
			}
		}
		return null;
	}

	// Listener

	private void onTasks(List<? extends ICommonTask> tasks, ExecuteTaskListener executeTaskListener) {
		if (tasks != null && !tasks.isEmpty()) {
			ITaskCycleListener[] ls = eventListenerList.getListeners(ITaskCycleListener.class);
			for (ICommonTask task : tasks) {
				for (ITaskCycleListener l : ls) {
					executeTaskListener.execute(l, task);
				}
				ITaskService taskService = getTaskService(task);
				if (taskService != null) {
					executeTaskListener.execute(taskService, task);
				}
			}
		}
	}

	private static final ExecuteTaskListener TODO_EXECUTE_TASK_LISTENER = new ExecuteTaskListener() {
		@Override
		public void execute(ITaskCycleListener taskCycleListener, ICommonTask task) {
			taskCycleListener.onTodo(task);
		}
	};

	private void onTodoTasks(List<? extends ICommonTask> tasks) {
		onTasks(tasks, TODO_EXECUTE_TASK_LISTENER);
	}

	private static final ExecuteTaskListener CURRENT_EXECUTE_TASK_LISTENER = new ExecuteTaskListener() {
		@Override
		public void execute(ITaskCycleListener taskCycleListener, ICommonTask task) {
			taskCycleListener.onCurrent(task);
		}
	};

	private void onCurrentTasks(List<? extends ICommonTask> tasks) {
		onTasks(tasks, CURRENT_EXECUTE_TASK_LISTENER);
	}

	private static final ExecuteTaskListener DONE_EXECUTE_TASK_LISTENER = new ExecuteTaskListener() {
		@Override
		public void execute(ITaskCycleListener taskCycleListener, ICommonTask task) {
			taskCycleListener.onDone(task);
		}
	};

	private void onDoneTasks(List<? extends ICommonTask> tasks) {
		onTasks(tasks, DONE_EXECUTE_TASK_LISTENER);
	}

	private static final ExecuteTaskListener NOTHING_EXECUTE_TASK_LISTENER = new ExecuteTaskListener() {
		@Override
		public void execute(ITaskCycleListener taskCycleListener, ICommonTask task) {
			taskCycleListener.onNothing(task);
		}
	};

	private void onNothingTasks(List<? extends ICommonTask> tasks) {
		onTasks(tasks, NOTHING_EXECUTE_TASK_LISTENER);
	}

	private static final ExecuteTaskListener DELETE_EXECUTE_TASK_LISTENER = new ExecuteTaskListener() {
		@Override
		public void execute(ITaskCycleListener taskCycleListener, ICommonTask task) {
			taskCycleListener.onDelete(task);
		}
	};

	private void onDeleteTasks(List<ICommonTask> tasks) {
		onTasks(tasks, DELETE_EXECUTE_TASK_LISTENER);
	}

	// Inner class

	private static class TasksLists {

		List<ICommonTask> tasksToRemoves;

		List<ICommonTask> newCurrentTasks;

	}

	private interface ExecuteTaskListener {

		void execute(ITaskCycleListener taskCycleListener, ICommonTask task);

	}

	private static class MyEngineContext implements ITaskService.IEngineContext {

		private final ITaskCluster currentTaskCluster;

		private final ITaskDefinition taskDefinition;

		private boolean lock;

		public MyEngineContext(ITaskCluster currentTaskCluster, ITaskDefinition taskDefinition) {
			super();

			this.currentTaskCluster = currentTaskCluster;
			this.taskDefinition = taskDefinition;

			this.lock = false;
		}

		private void verifyBlock() {
			if (lock) {
				throw new LockedEngineContextException();
			}
		}

		@Override
		public ITaskCluster getCurrentTaskCluster() {
			return currentTaskCluster;
		}

		@Override
		public ITaskDefinition getTaskDefinition() {
			return taskDefinition;
		}

		@Override
		public void startEngine(TaskClusterCallback taskClusterCallback, ITaskObject... taskObjects) {
			verifyBlock();
		}

		@Override
		public void startEngine(ITaskCluster... taskClusters) {
			verifyBlock();
		}

		@Override
		public void addTaskObjectsToTaskCluster(ITaskObject... taskObjects) {
			addTaskObjectsToTaskCluster(currentTaskCluster, taskObjects);
		}

		@Override
		public void addTaskObjectsToTaskCluster(ITaskCluster taskCluster, ITaskObject... taskObjects) {
			verifyBlock();
		}

		@Override
		public void removeTaskObjectsFromTaskCluster(ITaskObject... taskObjects) {
			verifyBlock();
		}

		@Override
		public void moveTaskObjectsToNewTaskCluster(TaskClusterCallback taskClusterCallback, ITaskObject... taskObjects) {
			verifyBlock();
		}

		@Override
		public void moveTaskObjectsToTaskCluster(ITaskObject... taskObjects) {
			moveTaskObjectsToTaskCluster(currentTaskCluster, taskObjects);
		}

		@Override
		public void moveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, ITaskObject... taskObjects) {
			verifyBlock();
		}
	}
}
