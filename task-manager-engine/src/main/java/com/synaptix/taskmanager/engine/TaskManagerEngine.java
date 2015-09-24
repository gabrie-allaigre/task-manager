package com.synaptix.taskmanager.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.synaptix.taskmanager.model.domains.ServiceNature;

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

	public void addTaskManagerListener(ITaskCycleListener taskManagerListener) {
		eventListenerList.add(ITaskCycleListener.class, taskManagerListener);
	}

	public void removeTaskManagerListener(ITaskCycleListener taskManagerListener) {
		eventListenerList.remove(ITaskCycleListener.class, taskManagerListener);
	}

	// Start engine

	/**
	 * Starts engine, creates cluster if cluster does not exist
	 * 
	 * @param taskObject
	 * @return
	 */
	public ITaskCluster startEngine(ITaskObject<?> taskObject) {
		if (taskObject == null) {
			return null;
		}
		// If cluster not existe, create
		ITaskCluster taskCluster = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
		if (taskCluster == null) {
			taskCluster = createTaskCluster(taskObject);
		}

		startEngine(taskCluster);

		return taskCluster;
	}

	/**
	 * 
	 * @param taskCluster
	 * @return
	 */
	public void startEngine(ITaskCluster taskCluster) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - StartEngine");
		}

		if (taskCluster == null || taskCluster.isCheckArchived()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM - Nothing, cluster is null or archived");
			}
			return;
		}

		LinkedList<ITaskCluster> restartClusters = new LinkedList<ITaskCluster>();
		restartClusters.addFirst(taskCluster);
		while (!restartClusters.isEmpty()) {
			taskCluster = restartClusters.removeFirst();

			boolean restart = false;

			// Find all current task for cluster
			List<AbstractTask> tasks = getTaskManagerConfiguration().getTaskManagerReader().findCurrentTasksByTaskCluster(taskCluster);

			if (tasks == null || tasks.isEmpty()) {
				if (taskCluster != null && !taskCluster.isCheckGraphCreated()) {
					createTaskGraphs(taskCluster);
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
								ITaskService.IExecutionResult executionResult = taskService.execute(task);
								if (executionResult == null) {
									throw new NullTaskExecutionException();
								} else {
									done = executionResult.isFinished();
									taskServiceResult = executionResult.getResult();
									if (executionResult.mustStopAndRestartTaskManager()) {
										restart = true;
									}
								}
							} catch (Throwable t) {
								LOG.error("TM - Error taskService = " + taskDefinition.getCode(), t);
								errorMessage = t;
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
								for (Iterator<AbstractTask> iterator = recycleList.iterator(); iterator.hasNext();) {
									AbstractTask iTask = iterator.next();
									if (idTask.equals(iTask)) {
										iterator.remove();
										break;
									}
								}
								for (Iterator<AbstractTask> iterator = tasksQueue.iterator(); iterator.hasNext();) {
									AbstractTask iTask = iterator.next();
									if (idTask.equals(iTask)) {
										iterator.remove();
										break;
									}
								}
							}
						}

						if (taskService != null && !taskService.getNature().equals(ServiceNature.DATA_CHECK)) {
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

	/*
	 * Set task as nothing
	 */
	private void setTaskNothing(ITaskCluster taskCluster, AbstractTask task, Object taskServiceResult, Throwable throwable) {
		getTaskManagerConfiguration().getTaskManagerWriter().saveNothingTask(taskCluster, task, taskServiceResult, throwable);

		onNothingTasks(Arrays.<AbstractTask> asList(task));
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

			Class<? extends ITaskObject<?>> taskObjectClass = updateStatusTask.getTaskObjectClass();

			if (!updateStatusTask.getOtherStatusTasksMap().isEmpty()) {
				for (Entry<Object, List<? extends AbstractTask>> entry : updateStatusTask.getOtherStatusTasksMap().entrySet()) {
					toDeleteTasks.addAll(extractAllTasks(entry.getValue()));
				}
			}

			List<IStatusGraph<Object>> statusGraphs = getTaskManagerConfiguration().getStatusGraphsRegistry().getNextStatusGraphsByTaskObjectType((Class<ITaskObject<Object>>) taskObjectClass,
					updateStatusTask.getCurrentStatus());
			if (statusGraphs != null && !statusGraphs.isEmpty()) {
				ITaskObjectManager<?> taskObjectManager = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(taskObjectClass);

				List<UpdateStatusTask> nextUpdateStatusTasks = new ArrayList<UpdateStatusTask>();
				Map<Object, List<? extends AbstractTask>> map = new HashMap<Object, List<? extends AbstractTask>>();

				for (IStatusGraph<Object> statusGraph : statusGraphs) {
					String taskChainCriteria = taskObjectManager.getTaskChainCriteria(updateStatusTask, statusGraph.getPreviousStatus(), statusGraph.getCurrentStatus());
					List<NormalTask> nextNormalTasks = getTaskManagerConfiguration().getTaskChainCriteriaBuilder().transformeToTasks(getTaskManagerConfiguration(), taskChainCriteria);

					IUpdateStatusTaskDefinition updateStatusTaskDefinition = getTaskManagerConfiguration().getTaskDefinitionRegistry()
							.getUpdateStatusTaskDefinition(statusGraph.getUpdateStatusTaskServiceCode());
					UpdateStatusTask nextUpdateStatusTask = getTaskManagerConfiguration().getTaskFactory().newUpdateStatusTask(updateStatusTaskDefinition, taskObjectClass,
							statusGraph.getCurrentStatus(), updateStatusTask);

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
	@SuppressWarnings("unchecked")
	private ITaskCluster createTaskCluster(ITaskObject<?> taskObject) {
		ITaskCluster taskCluster = getTaskManagerConfiguration().getTaskFactory().newTaskCluster();
		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewTaskCluster(taskCluster);

		UpdateStatusTask task = createInitTask(taskCluster, taskObject);

		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewGraphForTaskCluster(taskCluster, Arrays.asList(Pair.<ITaskObject<?>, UpdateStatusTask> of(taskObject, task)));

		onTodoTasks(Arrays.<AbstractTask> asList(task));
		onCurrentTasks(Arrays.<AbstractTask> asList(task));

		return taskCluster;
	}

	/*
	 * Create status graph and tasks
	 */
	private <F extends ITaskObject<?>> UpdateStatusTask createInitTask(ITaskCluster taskCluster, F taskObject) {
		ITaskObjectManager<F> taskObjectManager = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(taskObject);
		Class<F> taskObjectClass = taskObjectManager.getTaskObjectClass();

		// Create a first task, it does nothing
		UpdateStatusTask initTask = getTaskManagerConfiguration().getTaskFactory().newUpdateStatusTask(null, taskObjectClass, taskObject.getStatus(), null);

		return initTask;
	}

	/*
	 * Create Task graphs for task cluster
	 */
	private ITaskCluster createTaskGraphs(ITaskCluster taskCluster) {
		List<ITaskObject<?>> taskObjects = getTaskManagerConfiguration().getTaskManagerReader().findTaskObjectsByTaskCluster(taskCluster);

		List<UpdateStatusTask> updateStatusTasks = null;
		List<Pair<ITaskObject<?>, UpdateStatusTask>> taskObjectNodes = new ArrayList<Pair<ITaskObject<?>, UpdateStatusTask>>();
		if (taskObjects != null && !taskObjects.isEmpty()) {
			updateStatusTasks = new ArrayList<UpdateStatusTask>();
			for (ITaskObject<?> taskObject : taskObjects) {
				UpdateStatusTask initTask = createInitTask(taskCluster, taskObject);
				taskObjectNodes.add(Pair.<ITaskObject<?>, UpdateStatusTask> of(taskObject, initTask));
			}
		}

		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewGraphForTaskCluster(taskCluster, taskObjectNodes);

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

	private class TasksLists {

		public List<AbstractTask> tasksToRemoves;

		public List<AbstractTask> newCurrentTasks;

	}

	private interface ExecuteTaskListener {

		public void execute(ITaskCycleListener taskCycleListener, AbstractTask task);

	}
}
