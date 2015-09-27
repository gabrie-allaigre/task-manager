package com.synaptix.taskmanager.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.event.EventListenerList;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.listener.ITaskCycleListener;
import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.task.NormalTask;
import com.synaptix.taskmanager.engine.task.UpdateStatusTask;
import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.synaptix.taskmanager.engine.taskdefinition.IUpdateStatusTaskDefinition;
import com.synaptix.taskmanager.engine.taskservice.ITaskService;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

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
	public ITaskCluster startEngine(ITaskObject<?>... taskObjects) {
		if (taskObjects == null || taskObjects.length == 0) {
			return null;
		}

		Set<ITaskCluster> taskClusters = new HashSet<ITaskCluster>();
		Set<ITaskObject<?>> createClusters = new HashSet<ITaskObject<?>>();
		for (ITaskObject<?> taskObject : taskObjects) {
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
			res = createTaskCluster(createClusters.toArray(new ITaskObject<?>[createClusters.size()]));
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
			List<AbstractTask> tasks = getTaskManagerConfiguration().getTaskManagerReader().findCurrentTasksByTaskCluster(taskCluster);

			if (tasks == null || tasks.isEmpty()) {
				if (taskCluster != null && !taskCluster.isCheckGraphCreated()) {
					createTaskGraphsForTaskCluster(taskCluster);
					restartClusters.add(taskCluster);
				} else {
					getTaskManagerConfiguration().getTaskManagerWriter().archiveTaskCluster(taskCluster);
				}
			} else {
				LinkedList<AbstractTask> tasksQueue = new LinkedList<AbstractTask>(tasks);
				List<AbstractTask> recycleList = new ArrayList<AbstractTask>();

				while (!tasksQueue.isEmpty()) {
					AbstractTask task = tasksQueue.removeFirst();

					boolean done = true;
					Throwable errorMessage = null;
					ITaskService taskService = null;
					Object taskServiceResult = null;
					boolean noChanges = false;
					if (task.getTaskDefinition() != null) {
						ITaskDefinition taskDefinition = task.getTaskDefinition();
						taskService = taskDefinition.getTaskService();
						if (taskService == null) {
							if (LOG.isDebugEnabled()) {
								LOG.debug("TM - TaskService does not exist");
							}
							errorMessage = new NotFoundTaskDefinitionException();
						} else {
							if (LOG.isDebugEnabled()) {
								LOG.debug("TM - Execute taskService = " + taskDefinition.getCode());
							}
							try {
								MyEngineContext context = new MyEngineContext(taskCluster);

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
								LOG.error("TM - Error taskService = " + taskDefinition.getCode(), t);
								errorMessage = t;
								done = false;
							}

							if (LOG.isDebugEnabled()) {
								LOG.debug("TM - Finish " + taskDefinition.getCode() + (done ? " - Success" : " - Failure"));
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
							for (AbstractTask iTask : tasksLists.newCurrentTasks) {
								tasksQueue.addFirst(iTask);
							}
						}

						if (tasksLists.tasksToRemoves != null && !tasksLists.tasksToRemoves.isEmpty()) {
							for (AbstractTask idTask : tasksLists.tasksToRemoves) {
								for (Iterator<AbstractTask> iterator = recycleList.iterator(); iterator.hasNext(); ) {
									AbstractTask iTask = iterator.next();
									if (idTask.equals(iTask)) {
										iterator.remove();
										break;
									}
								}
								for (Iterator<AbstractTask> iterator = tasksQueue.iterator(); iterator.hasNext(); ) {
									AbstractTask iTask = iterator.next();
									if (idTask.equals(iTask)) {
										iterator.remove();
										break;
									}
								}
							}
						}

						if (!noChanges) {
							// Add previously failed tasks to end of deque. Not done when service nature is not DATA_CHECK because DATA_CHECK does not update objects.
							for (AbstractTask iTask : recycleList) {
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
	public void addTaskObjectsToTaskCluster(ITaskCluster taskCluster, ITaskObject<?>... taskObjects) {
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

		List<ITaskObject<?>> adds = new ArrayList<ITaskObject<?>>();
		for (ITaskObject<?> taskObject : taskObjects) {
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

		createTaskGraphForTaskCluster(taskCluster, adds.toArray(new ITaskObject<?>[adds.size()]));
		startEngine(taskCluster);
	}

	/**
	 * Remove task objects in task cluster and startEngine
	 *
	 * @param taskObjects
	 */
	public void removeTaskObjectsFromTaskCluster(ITaskObject<?>... taskObjects) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - removeTaskObjectsFromTaskCluster");
		}

		if (taskObjects == null || taskObjects.length == 0) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - Nothing, taskObject is null");
			}
			return;
		}

		Map<ITaskCluster, List<ITaskObject<?>>> modifyClusterMap = new HashMap<ITaskCluster, List<ITaskObject<?>>>();
		for (ITaskObject<?> taskObject : taskObjects) {
			if (taskObject != null) {
				ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
				if (tc != null) {
					List<ITaskObject<?>> tos = modifyClusterMap.get(tc);
					if (tos == null) {
						tos = new ArrayList<ITaskObject<?>>();
						modifyClusterMap.put(tc, tos);
					}
					if (!tos.contains(taskObject)) {
						tos.add(taskObject);
					}
				}
			}
		}

		for (Entry<ITaskCluster, List<ITaskObject<?>>> entry : modifyClusterMap.entrySet()) {
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
	public ITaskCluster moveTaskObjectsToNewTaskCluster(ITaskObject<?>... taskObjects) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - moveTaskObjectsToNewTaskCluster");
		}

		if (taskObjects == null || taskObjects.length == 0) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - Nothing, taskObject is null");
			}
			return null;
		}

		Map<ITaskCluster, List<ITaskObject<?>>> modifyClusterMap = new HashMap<ITaskCluster, List<ITaskObject<?>>>();
		for (ITaskObject<?> taskObject : taskObjects) {
			if (taskObject != null) {
				ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
				if (tc != null) {
					List<ITaskObject<?>> tos = modifyClusterMap.get(tc);
					if (tos == null) {
						tos = new ArrayList<ITaskObject<?>>();
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
	public void moveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, ITaskObject<?>... taskObjects) {
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

		Map<ITaskCluster, List<ITaskObject<?>>> modifyClusterMap = new HashMap<ITaskCluster, List<ITaskObject<?>>>();
		for (ITaskObject<?> taskObject : taskObjects) {
			if (taskObject != null) {
				ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
				if (tc != null && !tc.equals(dstTaskCluster)) {
					List<ITaskObject<?>> tos = modifyClusterMap.get(tc);
					if (tos == null) {
						tos = new ArrayList<ITaskObject<?>>();
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
	private void setTaskNothing(ITaskCluster taskCluster, AbstractTask task, Object taskServiceResult, Throwable throwable) {
		getTaskManagerConfiguration().getTaskManagerWriter().saveNothingTask(taskCluster, task, taskServiceResult, throwable);

		onNothingTasks(Arrays.<AbstractTask>asList(task));
	}

	/*
	 * Set task Done and move others tasks
	 */
	private TasksLists setTaskDone(ITaskCluster taskCluster, AbstractTask task, Object taskServiceResult) {
		return nextTasks(taskCluster, task, taskServiceResult, false);
	}

	@SuppressWarnings("unchecked")
	private TasksLists nextTasks(ITaskCluster taskCluster, AbstractTask toDoneTask, Object taskServiceResult, boolean skip) {
		TasksLists tasksLists = new TasksLists();

		List<AbstractTask> nextTodoTasks = new ArrayList<AbstractTask>();
		List<AbstractTask> newNextCurrentTasks = new ArrayList<AbstractTask>();
		List<AbstractTask> toDeleteTasks = new ArrayList<AbstractTask>();
		if (toDoneTask instanceof UpdateStatusTask) {
			UpdateStatusTask updateStatusTask = (UpdateStatusTask) toDoneTask;

			Class<? extends ITaskObject<Object>> taskObjectClass = (Class<? extends ITaskObject<Object>>) updateStatusTask.getTaskObjectClass();

			if (!updateStatusTask.getOtherStatusTasksMap().isEmpty()) {
				for (Entry<Object, List<? extends AbstractTask>> entry : updateStatusTask.getOtherStatusTasksMap().entrySet()) {
					toDeleteTasks.addAll(extractAllTasks(entry.getValue()));
				}
			}

			ITaskObjectManager<Object, ? extends ITaskObject<Object>> taskObjectManager = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(taskObjectClass);
			List<IStatusGraph<Object>> statusGraphs = taskObjectManager.getNextStatusGraphsByTaskObjectType(updateStatusTask, updateStatusTask.getCurrentStatus());
			if (statusGraphs != null && !statusGraphs.isEmpty()) {
				List<UpdateStatusTask> nextUpdateStatusTasks = new ArrayList<UpdateStatusTask>();
				Map<Object, List<? extends AbstractTask>> map = new HashMap<Object, List<? extends AbstractTask>>();

				for (IStatusGraph<Object> statusGraph : statusGraphs) {
					String taskChainCriteria = taskObjectManager.getTaskChainCriteria(updateStatusTask, statusGraph.getPreviousStatus(), statusGraph.getCurrentStatus());
					List<NormalTask> nextNormalTasks = getTaskManagerConfiguration().getTaskChainCriteriaBuilder().transformeToTasks(getTaskManagerConfiguration(), taskChainCriteria);

					IUpdateStatusTaskDefinition updateStatusTaskDefinition = getTaskManagerConfiguration().getTaskDefinitionRegistry()
							.getUpdateStatusTaskDefinition(statusGraph.getUpdateStatusTaskServiceCode());
					UpdateStatusTask nextUpdateStatusTask = getTaskManagerConfiguration().getTaskFactory()
							.newUpdateStatusTask(updateStatusTaskDefinition, taskObjectClass, statusGraph.getCurrentStatus(), updateStatusTask);

					if (nextNormalTasks != null && !nextNormalTasks.isEmpty()) {
						addUpdateStatusTaskInLast(nextNormalTasks, nextUpdateStatusTask);

						nextTodoTasks.addAll(extractAllTasks(nextNormalTasks));
						newNextCurrentTasks.addAll(nextNormalTasks);

						map.put(statusGraph.getCurrentStatus(), nextNormalTasks);
					} else {
						nextTodoTasks.add(nextUpdateStatusTask);
						newNextCurrentTasks.add(nextUpdateStatusTask);

						map.put(statusGraph.getCurrentStatus(), Arrays.asList(nextUpdateStatusTask));
					}

					nextUpdateStatusTasks.add(nextUpdateStatusTask);
				}

				for (UpdateStatusTask task : nextUpdateStatusTasks) {
					for (Entry<Object, List<? extends AbstractTask>> entry : map.entrySet()) {
						if (!entry.getKey().equals(task.getCurrentStatus())) {
							task.getOtherStatusTasksMap().put(entry.getKey(), entry.getValue());
						}
					}
				}
			}

			getTaskManagerConfiguration().getTaskManagerWriter().saveNewNextTasksInTaskCluster(taskCluster, updateStatusTask, taskServiceResult, newNextCurrentTasks, toDeleteTasks);
		} else if (toDoneTask instanceof NormalTask) {
			newNextCurrentTasks.addAll(((NormalTask) toDoneTask).getNextTasks());

			getTaskManagerConfiguration().getTaskManagerWriter().saveNextTasksInTaskCluster(taskCluster, toDoneTask, taskServiceResult, newNextCurrentTasks);
		}

		onDoneTasks(Arrays.asList(toDoneTask));
		onTodoTasks(nextTodoTasks);
		onCurrentTasks(newNextCurrentTasks);
		onDeleteTasks(toDeleteTasks);

		tasksLists.newCurrentTasks = newNextCurrentTasks;
		tasksLists.tasksToRemoves = toDeleteTasks;

		return tasksLists;
	}

	/*
	 * Add update status task in the last tasks
	 */
	private void addUpdateStatusTaskInLast(List<? extends AbstractTask> tasks, UpdateStatusTask updateStatusTask) {
		if (tasks != null && !tasks.isEmpty()) {
			for (AbstractTask task : tasks) {
				if (task instanceof NormalTask) {
					NormalTask normalTask = (NormalTask) task;
					if (normalTask.getNextTasks().isEmpty()) {
						normalTask.getNextTasks().add(updateStatusTask);
					} else {
						addUpdateStatusTaskInLast(normalTask.getNextTasks(), updateStatusTask);
					}
				}
			}
		}
	}

	/*
	 * Extract all tasks
	 */
	private List<AbstractTask> extractAllTasks(List<? extends AbstractTask> tasks) {
		List<AbstractTask> res = new ArrayList<AbstractTask>();
		if (tasks != null && !tasks.isEmpty()) {
			for (AbstractTask task : tasks) {
				res.add(task);
				if (task instanceof NormalTask) {
					NormalTask normalTask = (NormalTask) task;
					res.addAll(extractAllTasks(normalTask.getNextTasks()));
				}
			}
		}
		return res;
	}

	// Task creation

	/*
	 * Create task cluster for taskobject, create new task
	 */
	private ITaskCluster createTaskCluster(ITaskObject<?>... taskObjects) {
		ITaskCluster taskCluster = getTaskManagerConfiguration().getTaskFactory().newTaskCluster();
		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewTaskCluster(taskCluster);

		return createTaskGraphForTaskCluster(taskCluster, taskObjects);
	}

	/*
	 * Create status graph and tasks
	 */
	private <E extends Object, F extends ITaskObject<E>> UpdateStatusTask createInitTask(F taskObject) {
		ITaskObjectManager<E, F> taskObjectManager = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(taskObject);
		Class<F> taskObjectClass = taskObjectManager.getTaskObjectClass();

		// Create a first task, it does nothing
		UpdateStatusTask initTask = getTaskManagerConfiguration().getTaskFactory().newUpdateStatusTask(null, taskObjectClass, taskObject.getStatus(), null);

		return initTask;
	}

	/*
	 * Create Task graphs for task cluster
	 */
	private ITaskCluster createTaskGraphsForTaskCluster(ITaskCluster taskCluster) {
		List<ITaskObject<?>> taskObjects = getTaskManagerConfiguration().getTaskManagerReader().findTaskObjectsByTaskCluster(taskCluster);

		List<UpdateStatusTask> updateStatusTasks = null;
		List<Pair<ITaskObject<?>, UpdateStatusTask>> taskObjectNodes = new ArrayList<Pair<ITaskObject<?>, UpdateStatusTask>>();
		if (taskObjects != null && !taskObjects.isEmpty()) {
			updateStatusTasks = new ArrayList<UpdateStatusTask>();
			for (ITaskObject<?> taskObject : taskObjects) {
				UpdateStatusTask initTask = createInitTask(taskObject);
				taskObjectNodes.add(Pair.<ITaskObject<?>, UpdateStatusTask>of(taskObject, initTask));
			}
		}

		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewGraphFromTaskCluster(taskCluster, taskObjectNodes);

		onTodoTasks(updateStatusTasks);
		onCurrentTasks(updateStatusTasks);

		return taskCluster;
	}

	/*
	 * Create Task graph for task object and add in task cluster
	 */
	private ITaskCluster createTaskGraphForTaskCluster(ITaskCluster taskCluster, ITaskObject<?>... taskObjects) {
		List<UpdateStatusTask> updateStatusTasks = null;
		List<Pair<ITaskObject<?>, UpdateStatusTask>> taskObjectNodes = new ArrayList<Pair<ITaskObject<?>, UpdateStatusTask>>();
		if (taskObjects != null && taskObjects.length > 0) {
			updateStatusTasks = new ArrayList<UpdateStatusTask>();
			for (ITaskObject<?> taskObject : taskObjects) {
				UpdateStatusTask initTask = createInitTask(taskObject);
				taskObjectNodes.add(Pair.<ITaskObject<?>, UpdateStatusTask>of(taskObject, initTask));
			}
		}

		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewGraphFromTaskCluster(taskCluster, taskObjectNodes);

		onTodoTasks(updateStatusTasks);
		onCurrentTasks(updateStatusTasks);

		return taskCluster;
	}

	// Listener

	private void onTasks(List<? extends AbstractTask> tasks, ExecuteTaskListener executeTaskListener) {
		if (tasks != null && !tasks.isEmpty()) {
			ITaskCycleListener[] ls = eventListenerList.getListeners(ITaskCycleListener.class);
			for (AbstractTask task : tasks) {
				for (ITaskCycleListener l : ls) {
					executeTaskListener.execute(l, task);
				}
				if (task.getTaskDefinition() != null && task.getTaskDefinition().getTaskService() != null) {
					executeTaskListener.execute(task.getTaskDefinition().getTaskService(), task);
				}
			}
		}
	}

	private static final ExecuteTaskListener TODO_EXECUTE_TASK_LISTENER = new ExecuteTaskListener() {
		@Override
		public void execute(ITaskCycleListener taskCycleListener, AbstractTask task) {
			taskCycleListener.onTodo(task);
		}
	};

	private void onTodoTasks(List<? extends AbstractTask> tasks) {
		onTasks(tasks, TODO_EXECUTE_TASK_LISTENER);
	}

	private static final ExecuteTaskListener CURRENT_EXECUTE_TASK_LISTENER = new ExecuteTaskListener() {
		@Override
		public void execute(ITaskCycleListener taskCycleListener, AbstractTask task) {
			taskCycleListener.onCurrent(task);
		}
	};

	private void onCurrentTasks(List<? extends AbstractTask> tasks) {
		onTasks(tasks, CURRENT_EXECUTE_TASK_LISTENER);
	}

	private static final ExecuteTaskListener DONE_EXECUTE_TASK_LISTENER = new ExecuteTaskListener() {
		@Override
		public void execute(ITaskCycleListener taskCycleListener, AbstractTask task) {
			taskCycleListener.onDone(task);
		}
	};

	private void onDoneTasks(List<? extends AbstractTask> tasks) {
		onTasks(tasks, DONE_EXECUTE_TASK_LISTENER);
	}

	private static final ExecuteTaskListener NOTHING_EXECUTE_TASK_LISTENER = new ExecuteTaskListener() {
		@Override
		public void execute(ITaskCycleListener taskCycleListener, AbstractTask task) {
			taskCycleListener.onNothing(task);
		}
	};

	private void onNothingTasks(List<? extends AbstractTask> tasks) {
		onTasks(tasks, NOTHING_EXECUTE_TASK_LISTENER);
	}

	private static final ExecuteTaskListener DELETE_EXECUTE_TASK_LISTENER = new ExecuteTaskListener() {
		@Override
		public void execute(ITaskCycleListener taskCycleListener, AbstractTask task) {
			taskCycleListener.onDelete(task);
		}
	};

	private void onDeleteTasks(List<AbstractTask> tasks) {
		onTasks(tasks, DELETE_EXECUTE_TASK_LISTENER);
	}

	// Inner class

	private static class TasksLists {

		List<AbstractTask> tasksToRemoves;

		List<AbstractTask> newCurrentTasks;

	}

	private interface ExecuteTaskListener {

		void execute(ITaskCycleListener taskCycleListener, AbstractTask task);

	}

	private static class MyEngineContext implements ITaskService.IEngineContext {

		private final ITaskCluster currentTaskCluster;

		private boolean lock;

		public MyEngineContext(ITaskCluster currentTaskCluster) {
			super();

			this.currentTaskCluster = currentTaskCluster;

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
		public void startEngine(TaskClusterCallback taskClusterCallback, ITaskObject<?>... taskObjects) {
			verifyBlock();
		}

		@Override
		public void startEngine(ITaskCluster... taskClusters) {
			verifyBlock();
		}

		@Override
		public void addTaskObjectsToTaskCluster(ITaskObject<?>... taskObjects) {
			addTaskObjectsToTaskCluster(currentTaskCluster, taskObjects);
		}

		@Override
		public void addTaskObjectsToTaskCluster(ITaskCluster taskCluster, ITaskObject<?>... taskObjects) {
			verifyBlock();
		}

		@Override
		public void removeTaskObjectsFromTaskCluster(ITaskObject<?>... taskObjects) {
			verifyBlock();
		}

		@Override
		public void moveTaskObjectsToNewTaskCluster(TaskClusterCallback taskClusterCallback, ITaskObject<?>... taskObjects) {
			verifyBlock();
		}

		@Override
		public void moveTaskObjectsToTaskCluster(ITaskObject<?>... taskObjects) {
			moveTaskObjectsToTaskCluster(currentTaskCluster, taskObjects);
		}

		@Override
		public void moveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, ITaskObject<?>... taskObjects) {
			verifyBlock();
		}
	}
}
