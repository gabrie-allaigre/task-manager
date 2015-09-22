package com.synaptix.taskmanager.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.component.model.IServiceResult;
import com.synaptix.taskmanager.antlr.AbstractGraphNode;
import com.synaptix.taskmanager.antlr.GraphCalcHelper;
import com.synaptix.taskmanager.antlr.IdGraphNode;
import com.synaptix.taskmanager.antlr.NextGraphNode;
import com.synaptix.taskmanager.antlr.ParallelGraphNode;
import com.synaptix.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.synaptix.taskmanager.engine.configuration.result.ServiceResultBuilder;
import com.synaptix.taskmanager.error.TaskManagerErrorEnum;
import com.synaptix.taskmanager.manager.AbstractTask;
import com.synaptix.taskmanager.manager.ITaskObjectManager;
import com.synaptix.taskmanager.manager.NormalTask;
import com.synaptix.taskmanager.manager.UpdateStatusTask;
import com.synaptix.taskmanager.manager.graph.IStatusGraph;
import com.synaptix.taskmanager.manager.taskdefinition.INormalTaskDefinition;
import com.synaptix.taskmanager.manager.taskdefinition.ITaskDefinition;
import com.synaptix.taskmanager.manager.taskdefinition.IUpdateStatusTaskDefinition;
import com.synaptix.taskmanager.manager.taskservice.ITaskService;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;

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
						LOG.debug("TM - taskService is null");
					}
				}

				if (done) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("TM - task is done");
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
						LOG.debug("TM - task is nothing");
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

		// serviceResultBuilder.ingest(restart());
		return serviceResultBuilder.compileResult(null);
	}

	/*
	 * Set task as nothing
	 */
	private void setTaskNothing(ITaskCluster taskCluster, AbstractTask task, Object taskServiceResult, Throwable errorMessage) {
		getTaskManagerConfiguration().getTaskManagerWriter().saveNothingTask(taskCluster, task, taskServiceResult, errorMessage);
	}

	/*
	 * Set task Done and move others tasks
	 */
	private TasksLists setTaskDone(ITaskCluster taskCluster, AbstractTask task, Object taskServiceResult) {
		return nextTasks(taskCluster, task, taskServiceResult, false);
	}

	private TasksLists nextTasks(ITaskCluster taskCluster, AbstractTask toDoneTask, Object taskServiceResult, boolean skip) {
		TasksLists tasksLists = new TasksLists();
		if (toDoneTask instanceof UpdateStatusTask) {
			UpdateStatusTask updateStatusTask = (UpdateStatusTask) toDoneTask;

			Class<? extends ITaskObject<?>> taskObjectClass = updateStatusTask.getTaskObjectClass();
			// if (task.getIdPreviousUpdateStatusTask() != null) {
			// // Cancel tasks of other branches
			// tasksLists.tasksToRemoves.addAll(deleteOtherChildPreviousUpdateStatusTasks(task));
			// }

			List<IStatusGraph> statusGraphs = getTaskManagerConfiguration().getStatusGraphsRegistry().getNextStatusGraphsByTaskObjectType(taskObjectClass, updateStatusTask.getCurrentStatus());
			if (statusGraphs != null && !statusGraphs.isEmpty()) {
				ITaskObjectManager<?> taskObjectManager = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(taskObjectClass);

				for (IStatusGraph statusGraph : statusGraphs) {
					System.out.println(statusGraph);

					String taskChainCriteria = taskObjectManager.getTaskChainCriteria(updateStatusTask, statusGraph.getPreviousStatus(), statusGraph.getCurrentStatus());
					List<NormalTask> nextNormalTasks = _createTasks(taskChainCriteria);

					IUpdateStatusTaskDefinition updateStatusTaskDefinition = getTaskManagerConfiguration().getTaskDefinitionRegistry()
							.getUpdateStatusTaskDefinition(statusGraph.getUpdateStatusTaskServiceCode());
					UpdateStatusTask nextUpdateStatusTask = getTaskManagerConfiguration().getTaskFactory().newUpdateStatusTask(updateStatusTaskDefinition, taskObjectClass,
							statusGraph.getCurrentStatus(), updateStatusTask);

					tasksLists.newCurrentTasks = new ArrayList<AbstractTask>();
					if (nextNormalTasks != null && !nextNormalTasks.isEmpty()) {

					} else {
						tasksLists.newCurrentTasks.add(nextUpdateStatusTask);
					}
				}
			}

		} else if (toDoneTask instanceof NormalTask) {
			List<AbstractTask> nextCurrentTasks = ((NormalTask) toDoneTask).getNextTasks();

			getTaskManagerConfiguration().getTaskManagerWriter().saveNextTasksInTaskCluster(taskCluster, toDoneTask, taskServiceResult, nextCurrentTasks);

			tasksLists.newCurrentTasks = nextCurrentTasks;
			tasksLists.tasksToRemoves = null;
		}
		return tasksLists;
	}

	// Task creation

	@SuppressWarnings("unchecked")
	private ITaskCluster createTaskCluster(ITaskObject<?> taskObject) {
		ITaskCluster taskCluster = getTaskManagerConfiguration().getTaskFactory().newTaskCluster();
		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewTaskCluster(taskCluster);

		UpdateStatusTask task = createInitTask(taskCluster, taskObject);

		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewGraphForTaskCluster(taskCluster, Arrays.asList(Pair.<ITaskObject<?>, UpdateStatusTask> of(taskObject, task)));

		// TODO

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

		List<Pair<ITaskObject<?>, UpdateStatusTask>> taskObjectNodes = new ArrayList<Pair<ITaskObject<?>, UpdateStatusTask>>();
		if (taskObjects != null && !taskObjects.isEmpty()) {
			for (ITaskObject<?> taskObject : taskObjects) {
				taskObjectNodes.add(Pair.<ITaskObject<?>, UpdateStatusTask> of(taskObject, createInitTask(taskCluster, taskObject)));
			}
		}

		taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveNewGraphForTaskCluster(taskCluster, taskObjectNodes);

		return taskCluster;
	}

	/*
	 * Create a tasks for parent and task chain criteria
	 */
	private List<NormalTask> _createTasks(String taskChainCriteria) {
		if (taskChainCriteria != null && !taskChainCriteria.isEmpty()) {
			AbstractGraphNode graphNode = GraphCalcHelper.buildGraphRule(taskChainCriteria);
			return _createTasks(graphNode);
		}
		return null;
	}

	private List<NormalTask> _createTasks(AbstractGraphNode node) {
		if (node instanceof IdGraphNode) {
			IdGraphNode ign = (IdGraphNode) node;
			INormalTaskDefinition normalTaskDefinition = getTaskManagerConfiguration().getTaskDefinitionRegistry().getNormalTaskDefinition(ign.getId());
			NormalTask task = getTaskManagerConfiguration().getTaskFactory().newNormalTask(normalTaskDefinition);
			return Arrays.asList(task);
		} else if (node instanceof ParallelGraphNode) {
			ParallelGraphNode pgn = (ParallelGraphNode) node;

			List<NormalTask> taskNodes = new ArrayList<NormalTask>();
			for (AbstractGraphNode subNode : pgn.getNodes()) {
				taskNodes.addAll(_createTasks(subNode));
			}

			return taskNodes;
		} else if (node instanceof NextGraphNode) {
			NextGraphNode ngn = (NextGraphNode) node;

			List<NormalTask> firstCr = _createTasks(ngn.getFirstNode());
			List<NormalTask> nextCr = _createTasks(ngn.getNextNode());

			if (firstCr != null && nextCr != null) {
				for (NormalTask firstTask : firstCr) {
					for (NormalTask nextTask : nextCr) {
						firstTask.getNextTasks().add(nextTask);
					}
				}
			}

			return firstCr;
		}
		return null;
	}

	// Inner class

	private class TasksLists {

		public List<AbstractTask> tasksToRemoves;

		public List<AbstractTask> newCurrentTasks;

	}
}
