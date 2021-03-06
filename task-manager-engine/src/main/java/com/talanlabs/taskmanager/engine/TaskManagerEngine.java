package com.talanlabs.taskmanager.engine;

import com.talanlabs.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.talanlabs.taskmanager.engine.configuration.transform.ITaskChainCriteriaTransform;
import com.talanlabs.taskmanager.engine.graph.IStatusGraph;
import com.talanlabs.taskmanager.engine.listener.ITaskCycleListener;
import com.talanlabs.taskmanager.engine.manager.ITaskObjectManager;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.task.IStatusTask;
import com.talanlabs.taskmanager.engine.task.ISubTask;
import com.talanlabs.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.talanlabs.taskmanager.engine.taskservice.ITaskService;
import com.talanlabs.taskmanager.model.ITaskCluster;
import com.talanlabs.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TaskManagerEngine {

    private static final Log LOG = LogFactory.getLog(TaskManagerEngine.class);
    private static final ExecuteTaskListener TODO_EXECUTE_TASK_LISTENER = ITaskCycleListener::onTodo;
    private static final ExecuteTaskListener CURRENT_EXECUTE_TASK_LISTENER = ITaskCycleListener::onCurrent;
    private static final ExecuteTaskListener DONE_EXECUTE_TASK_LISTENER = ITaskCycleListener::onDone;
    private static final ExecuteTaskListener NOTHING_EXECUTE_TASK_LISTENER = ITaskCycleListener::onNothing;
    private static final ExecuteTaskListener DELETE_EXECUTE_TASK_LISTENER = ITaskCycleListener::onDelete;

    private ITaskManagerConfiguration taskManagerConfiguration;
    private EventListenerList eventListenerList;

    // Start engine

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

    public ITaskCycleListener[] getTaskCycleListeners() {
        return eventListenerList.getListeners(ITaskCycleListener.class);
    }

    /**
     * Start engine
     *
     * @param taskObjects task object
     * @return a cluster
     */
    public ITaskCluster startEngine(ITaskObject... taskObjects) {
        if (taskObjects == null || taskObjects.length == 0) {
            return null;
        }

        Set<ITaskCluster> taskClusters = new HashSet<>();
        Set<ITaskObject> createClusters = new HashSet<>();
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

    // Private Methods

    /**
     * Start engin for taskCluster
     *
     * @param taskClusters clusters
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

        LinkedList<ITaskCluster> restartClusters = new LinkedList<>();
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
                LinkedList<ICommonTask> tasksQueue = new LinkedList<>(tasks);
                List<ICommonTask> recycleList = new ArrayList<>();

                while (!tasksQueue.isEmpty()) {
                    ICommonTask task = tasksQueue.removeFirst();

                    restart = startEngineExecuteTask(taskCluster, task, tasksQueue, recycleList, restartClusters);
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

    private boolean startEngineExecuteTask(ITaskCluster taskCluster, ICommonTask task, LinkedList<ICommonTask> tasksQueue, List<ICommonTask> recycleList, LinkedList<ITaskCluster> restartClusters) {
        boolean restart = false;

        boolean done = true;
        Exception errorMessage = null;
        Object taskServiceResult = null;
        boolean noChanges = false;
        if (task.getCodeTaskDefinition() != null) {
            ITaskDefinition taskDefinition = getTaskManagerConfiguration().getTaskDefinitionRegistry().getTaskDefinition(task.getCodeTaskDefinition());
            if (taskDefinition == null || taskDefinition.getTaskService() == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("TM - TaskService does not exist code=" + task.getCodeTaskDefinition());
                }
                done = false;
                errorMessage = new NotFoundTaskDefinitionException();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("TM - Execute taskService code=" + task.getCodeTaskDefinition());
                }
                ITaskService taskService = taskDefinition.getTaskService();
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
                            executeContext(context, restartClusters);
                            restart = restartClusters.contains(taskCluster);
                        }
                    }
                } catch (Exception t) {
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

        startEngineDone(taskCluster, task, done, taskServiceResult, noChanges, errorMessage, tasksQueue, recycleList);

        return restart;
    }

    private void startEngineDone(ITaskCluster taskCluster, ICommonTask task, boolean done, Object taskServiceResult, boolean noChanges, Exception errorMessage, LinkedList<ICommonTask> tasksQueue,
            List<ICommonTask> recycleList) {
        if (done) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("TM - Task is done");
            }
            try {
                TasksLists tasksLists = setTaskDone(taskCluster, task, taskServiceResult);
                // Add new tasks to top of deque
                if (tasksLists.newCurrentTasks != null && !tasksLists.newCurrentTasks.isEmpty()) {
                    tasksLists.newCurrentTasks.forEach(tasksQueue::addFirst);
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
                    recycleList.forEach(tasksQueue::addLast);
                    recycleList.clear();
                }
            } catch (Exception t) {
                LOG.error("TM - Error setTaskDone " + task.getCodeTaskDefinition(), t);
                setTaskNothing(taskCluster, task, taskServiceResult, t);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("TM - Task did nothing");
            }

            setTaskNothing(taskCluster, task, taskServiceResult, errorMessage);

            recycleList.add(task);
        }
    }

    /**
     * Add task objects in task cluster and startEngine
     *
     * @param taskCluster cluster
     * @param taskObjects task object to add
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

        List<ITaskObject> adds = new ArrayList<>();
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
     * @param taskObjects task object to remove
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

        Map<ITaskCluster, List<ITaskObject>> modifyClusterMap = new HashMap<>();
        for (ITaskObject taskObject : taskObjects) {
            if (taskObject != null) {
                ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
                if (tc != null) {
                    List<ITaskObject> tos = modifyClusterMap.get(tc);
                    if (tos == null) {
                        tos = new ArrayList<>();
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
     * @param taskObjects move task object in new cluster
     * @return new cluster
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

        Map<ITaskCluster, List<ITaskObject>> modifyClusterMap = new HashMap<>();
        for (ITaskObject taskObject : taskObjects) {
            if (taskObject != null) {
                ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
                if (tc != null) {
                    List<ITaskObject> tos = modifyClusterMap.get(tc);
                    if (tos == null) {
                        tos = new ArrayList<>();
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

            taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveMoveTaskObjectsToTaskCluster(taskCluster, modifyClusterMap);

            List<ITaskCluster> cs = new ArrayList<>(modifyClusterMap.keySet());
            cs.add(taskCluster);
            startEngine(cs.toArray(new ITaskCluster[cs.size()]));
        }
        return taskCluster;
    }

    /**
     * Move task objects (with other cluster) to task cluster, start engin on all cluster
     *
     * @param dstTaskCluster destination cluster
     * @param taskObjects    task object to move cluster
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

        Map<ITaskCluster, List<ITaskObject>> modifyClusterMap = new HashMap<>();
        for (ITaskObject taskObject : taskObjects) {
            if (taskObject != null) {
                ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
                if (tc != null && !tc.equals(dstTaskCluster)) {
                    List<ITaskObject> tos = modifyClusterMap.get(tc);
                    if (tos == null) {
                        tos = new ArrayList<>();
                        modifyClusterMap.put(tc, tos);
                    }
                    if (!tos.contains(taskObject)) {
                        tos.add(taskObject);
                    }
                }
            }
        }

        if (!modifyClusterMap.isEmpty()) {
            getTaskManagerConfiguration().getTaskManagerWriter().saveMoveTaskObjectsToTaskCluster(dstTaskCluster, modifyClusterMap);

            List<ITaskCluster> cs = new ArrayList<>(modifyClusterMap.keySet());
            cs.add(dstTaskCluster);
            startEngine(cs.toArray(new ITaskCluster[cs.size()]));
        }
    }

    private void executeContext(MyEngineContext context, LinkedList<ITaskCluster> restartClusters) {
        context.lock = true;

        executeContextStartEngine(context, restartClusters);
        executeContextAddRemove(context, restartClusters);
        executeContextMove(context, restartClusters);
    }

    private void executeContextStartEngine(MyEngineContext context, LinkedList<ITaskCluster> restartClusters) {
        // Start engine from task objects
        context.startEngineTaskObjects.forEach(pair -> {
            Set<ITaskCluster> taskClusters = new HashSet<>();
            Set<ITaskObject> createClusters = new HashSet<>();
            pair.getLeft().stream().filter(taskObject -> taskObject != null).forEach(taskObject -> {
                // If cluster not existe, create
                ITaskCluster taskCluster = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
                if (taskCluster == null) {
                    createClusters.add(taskObject);
                } else {
                    taskClusters.add(taskCluster);
                }
            });

            ITaskCluster res = null;
            if (!createClusters.isEmpty()) {
                res = createTaskCluster(createClusters.toArray(new ITaskObject[createClusters.size()]));
                taskClusters.add(res);
            } else if (taskClusters.size() == 1) {
                res = taskClusters.iterator().next();
            }

            restartClusters.addAll(taskClusters);

            if (pair.getRight() != null) {
                pair.getRight().setTaskCluster(res);
            }
        });

        // Start engine from cluster
        restartClusters.addAll(context.startEngineTaskClusters);
    }

    private void executeContextAddRemove(MyEngineContext context, LinkedList<ITaskCluster> restartClusters) {
        // Add
        context.addToTaskClusters.forEach(pair -> {
            List<ITaskObject> adds = new ArrayList<>();
            pair.getRight().stream().filter(taskObject -> taskObject != null).forEach(taskObject -> {
                ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
                if (tc != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("TM - Nothing, taskObject is other or same in cluster");
                    }
                } else {
                    adds.add(taskObject);
                }
            });

            createTaskGraphForTaskCluster(pair.getLeft(), adds.toArray(new ITaskObject[adds.size()]));

            restartClusters.add(pair.getLeft());
        });

        // Remove
        Map<ITaskCluster, List<ITaskObject>> modifyClusterMap = new HashMap<>();
        context.removeFromTaskClusters.stream().filter(taskObject -> taskObject != null).forEach(taskObject -> {
            if (taskObject != null) {
                ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
                if (tc != null) {
                    List<ITaskObject> tos = modifyClusterMap.get(tc);
                    if (tos == null) {
                        tos = new ArrayList<>();
                        modifyClusterMap.put(tc, tos);
                    }
                    if (!tos.contains(taskObject)) {
                        tos.add(taskObject);
                    }
                }
            }
        });

        for (Entry<ITaskCluster, List<ITaskObject>> entry : modifyClusterMap.entrySet()) {
            getTaskManagerConfiguration().getTaskManagerWriter().saveRemoveTaskObjectsFromTaskCluster(entry.getKey(), entry.getValue());
        }

        restartClusters.addAll(modifyClusterMap.keySet());
    }

    // Task creation

    private void executeContextMove(MyEngineContext context, LinkedList<ITaskCluster> restartClusters) {
        // Move to new cluster
        context.moveToNewTaskClusters.forEach(pair -> {
            Map<ITaskCluster, List<ITaskObject>> modifyClusterMap = new HashMap<>();
            for (ITaskObject taskObject : pair.getLeft()) {
                if (taskObject != null) {
                    ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
                    if (tc != null) {
                        List<ITaskObject> tos = modifyClusterMap.get(tc);
                        if (tos == null) {
                            tos = new ArrayList<>();
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

                taskCluster = getTaskManagerConfiguration().getTaskManagerWriter().saveMoveTaskObjectsToTaskCluster(taskCluster, modifyClusterMap);

                List<ITaskCluster> cs = new ArrayList<>(modifyClusterMap.keySet());
                cs.add(taskCluster);
                restartClusters.addAll(cs);
            }
            if (pair.getRight() != null) {
                pair.getRight().setTaskCluster(taskCluster);
            }
        });

        // Move
        context.moveToTaskClusters.forEach(pair -> {
            Map<ITaskCluster, List<ITaskObject>> modifyClusterMap = new HashMap<>();
            ITaskCluster dstTaskCluster = pair.getLeft();
            for (ITaskObject taskObject : pair.getRight()) {
                if (taskObject != null) {
                    ITaskCluster tc = getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(taskObject);
                    if (tc != null && !tc.equals(pair.getLeft())) {
                        List<ITaskObject> tos = modifyClusterMap.get(tc);
                        if (tos == null) {
                            tos = new ArrayList<>();
                            modifyClusterMap.put(tc, tos);
                        }
                        if (!tos.contains(taskObject)) {
                            tos.add(taskObject);
                        }
                    }
                }
            }

            if (!modifyClusterMap.isEmpty()) {
                getTaskManagerConfiguration().getTaskManagerWriter().saveMoveTaskObjectsToTaskCluster(dstTaskCluster, modifyClusterMap);

                List<ITaskCluster> cs = new ArrayList<>(modifyClusterMap.keySet());
                cs.add(dstTaskCluster);
                restartClusters.addAll(cs);
            }
        });
    }

    /*
     * Set task as nothing
     */
    private void setTaskNothing(ITaskCluster taskCluster, ICommonTask task, Object taskServiceResult, Throwable throwable) {
        getTaskManagerConfiguration().getTaskManagerWriter().saveNothingTask(taskCluster, task, taskServiceResult, throwable);

        onNothingTasks(taskCluster, Collections.singletonList(task));
    }

    /*
     * Set task Done and move others tasks
     */
    private TasksLists setTaskDone(ITaskCluster taskCluster, ICommonTask task, Object taskServiceResult) {
        return nextTasks(taskCluster, task, taskServiceResult);
    }

    @SuppressWarnings("unchecked")
    private TasksLists nextTasks(ITaskCluster taskCluster, ICommonTask toDoneTask, Object taskServiceResult) {
        TasksLists tasksLists = new TasksLists();

        List<ICommonTask> nextTodoTasks = new ArrayList<>();
        List<ICommonTask> nextCurrentTasks = new ArrayList<>();
        List<ICommonTask> toDeleteTasks = new ArrayList<>();
        if (getTaskManagerConfiguration().getTaskFactory().isStatusTask(toDoneTask)) {
            nextTasksForStatusTask(taskCluster, toDoneTask, taskServiceResult, nextTodoTasks, nextCurrentTasks, toDeleteTasks);
        } else if (getTaskManagerConfiguration().getTaskFactory().isSubTask(toDoneTask)) {
            nextTasksForSubTask(taskCluster, toDoneTask, taskServiceResult, nextCurrentTasks);
        }

        onDoneTasks(taskCluster, Collections.singletonList(toDoneTask));
        onTodoTasks(taskCluster, nextTodoTasks);
        onCurrentTasks(taskCluster, nextCurrentTasks);
        onDeleteTasks(taskCluster, toDeleteTasks);

        tasksLists.newCurrentTasks = nextCurrentTasks;
        tasksLists.tasksToRemoves = toDeleteTasks;

        return tasksLists;
    }

    private void nextTasksForStatusTask(ITaskCluster taskCluster, ICommonTask toDoneTask, Object taskServiceResult, List<ICommonTask> nextTodoTasks, List<ICommonTask> nextCurrentTasks,
            List<ICommonTask> toDeleteTasks) {
        IStatusTask statusTask = (IStatusTask) toDoneTask;

        Class<? extends ITaskObject> taskObjectClass = statusTask.getTaskObjectClass();

        List<? extends ICommonTask> oldOtherPreviousNextTasks = getTaskManagerConfiguration().getTaskManagerReader().findOtherBranchFirstTasksByStatusTask(statusTask);

        if (oldOtherPreviousNextTasks != null && !oldOtherPreviousNextTasks.isEmpty()) {
            toDeleteTasks.addAll(extractAllTasks(oldOtherPreviousNextTasks));
        }

        ITaskObjectManager<Object, ? extends ITaskObject> taskObjectManager = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(taskObjectClass);
        List<IStatusGraph<Object>> statusGraphs = taskObjectManager.getNextStatusGraphsByTaskObjectType(statusTask, statusTask.getCurrentStatus());

        List<ICommonTask> newTasks = new ArrayList<>();
        Map<ISubTask, List<ICommonTask>> linkNextTasksMap = new HashMap<>();

        Map<IStatusTask, List<ICommonTask>> otherBranchFirstTasksMap = new HashMap<>();

        if (statusGraphs != null && !statusGraphs.isEmpty()) {
            List<IStatusTask> nextStatusTasks = new ArrayList<>();
            Map<Object, List<? extends ICommonTask>> map = new HashMap<>();

            for (IStatusGraph<Object> statusGraph : statusGraphs) {
                createSubTasks(taskObjectClass, taskObjectManager, statusGraph, statusTask, newTasks, linkNextTasksMap, nextStatusTasks, map, nextTodoTasks, nextCurrentTasks);
            }

            for (IStatusTask task : nextStatusTasks) {
                List<ICommonTask> otherBranchFirstTasks = new ArrayList<>();
                map.entrySet().stream().filter(entry -> !entry.getKey().equals(task.getCurrentStatus())).forEach(entry -> otherBranchFirstTasks.addAll(entry.getValue()));
                otherBranchFirstTasksMap.put(task, otherBranchFirstTasks);
            }
        }

        getTaskManagerConfiguration().getTaskManagerWriter()
                .saveNewNextTasksInTaskCluster(taskCluster, statusTask, taskServiceResult, newTasks, linkNextTasksMap, otherBranchFirstTasksMap, nextCurrentTasks, toDeleteTasks);
    }

    private void createSubTasks(Class<? extends ITaskObject> taskObjectClass, ITaskObjectManager<Object, ? extends ITaskObject> taskObjectManager, IStatusGraph<Object> statusGraph,
            IStatusTask statusTask, List<ICommonTask> newTasks, Map<ISubTask, List<ICommonTask>> linkNextTasksMap, List<IStatusTask> nextStatusTasks, Map<Object, List<? extends ICommonTask>> map,
            List<ICommonTask> nextTodoTasks, List<ICommonTask> nextCurrentTasks) {
        String taskChainCriteria = taskObjectManager.getTaskChainCriteria(statusTask, statusGraph.getPreviousStatus(), statusGraph.getCurrentStatus());
        ITaskChainCriteriaTransform.IResult result = getTaskManagerConfiguration().getTaskChainCriteriaBuilder().transformeToTasks(getTaskManagerConfiguration(), taskChainCriteria);

        IStatusTask nextstatusTask = getTaskManagerConfiguration().getTaskFactory().newStatusTask(statusGraph.getStatusTaskServiceCode(), taskObjectClass, statusGraph.getCurrentStatus());

        newTasks.add(nextstatusTask);

        if (result != null && result.getNewSubTasks() != null && !result.getNewSubTasks().isEmpty()) {
            for (ISubTask newSubTask : result.getNewSubTasks()) {
                List<ISubTask> nextTasks = result.getLinkNextTasksMap().get(newSubTask);
                if (nextTasks != null && !nextTasks.isEmpty()) {
                    linkNextTasksMap.put(newSubTask, new ArrayList<>(nextTasks));
                } else {
                    linkNextTasksMap.put(newSubTask, Collections.<ICommonTask>singletonList(nextstatusTask));
                }
            }

            newTasks.addAll(result.getNewSubTasks());

            nextTodoTasks.addAll(result.getNewSubTasks());
            nextCurrentTasks.addAll(result.getNextSubTasks());

            map.put(statusGraph.getCurrentStatus(), result.getNextSubTasks());
        } else {
            nextTodoTasks.add(nextstatusTask);
            nextCurrentTasks.add(nextstatusTask);

            map.put(statusGraph.getCurrentStatus(), Collections.singletonList(nextstatusTask));
        }

        nextStatusTasks.add(nextstatusTask);
    }

    private void nextTasksForSubTask(ITaskCluster taskCluster, ICommonTask toDoneTask, Object taskServiceResult, List<ICommonTask> nextCurrentTasks) {
        List<? extends ICommonTask> nextTasks = getTaskManagerConfiguration().getTaskManagerReader().findNextTasksBySubTask((ISubTask) toDoneTask, true);

        if (nextTasks != null && !nextTasks.isEmpty()) {
            nextCurrentTasks.addAll(nextTasks);
        }

        getTaskManagerConfiguration().getTaskManagerWriter().saveNextTasksInTaskCluster(taskCluster, toDoneTask, taskServiceResult, nextCurrentTasks);
    }

    /*
     * Extract all tasks
     */
    private List<? extends ICommonTask> extractAllTasks(List<? extends ICommonTask> tasks) {
        List<ICommonTask> res = new ArrayList<>();
        if (tasks != null && !tasks.isEmpty()) {
            for (ICommonTask task : tasks) {
                res.add(task);
                if (task instanceof ISubTask) {
                    ISubTask subTask = (ISubTask) task;
                    List<? extends ICommonTask> nextTasks = getTaskManagerConfiguration().getTaskManagerReader().findNextTasksBySubTask(subTask, false);
                    res.addAll(extractAllTasks(nextTasks));
                }
            }
        }
        return res;
    }

    // Listener

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
    private <E, F extends ITaskObject> IStatusTask createInitTask(F taskObject) {
        ITaskObjectManager<E, F> taskObjectManager = getTaskManagerConfiguration().getTaskObjectManagerRegistry().getTaskObjectManager(taskObject);
        Class<F> taskObjectClass = taskObjectManager.getTaskObjectClass();

        E currentStatus = taskObjectManager.getInitialStatus(taskObject);

        // Create a first task, it does nothing
        return getTaskManagerConfiguration().getTaskFactory().newStatusTask(null, taskObjectClass, currentStatus);
    }

    /*
     * Create Task graphs for task cluster
     */
    private ITaskCluster createTaskGraphsForTaskCluster(ITaskCluster taskCluster) {
        List<? extends ITaskObject> taskObjects = getTaskManagerConfiguration().getTaskManagerReader().findTaskObjectsByTaskCluster(taskCluster);

        return createTaskGraphForTaskCluster(taskCluster, taskObjects != null ? taskObjects.toArray(new ITaskObject[taskObjects.size()]) : null);
    }

    /*
     * Create Task graph for task object and add in task cluster
     */
    private ITaskCluster createTaskGraphForTaskCluster(ITaskCluster taskCluster, ITaskObject... taskObjects) {
        List<IStatusTask> statusTasks = null;
        List<Pair<ITaskObject, IStatusTask>> taskObjectNodes = new ArrayList<>();
        if (taskObjects != null && taskObjects.length > 0) {
            statusTasks = new ArrayList<>();
            for (ITaskObject taskObject : taskObjects) {
                IStatusTask initTask = createInitTask(taskObject);
                taskObjectNodes.add(Pair.of(taskObject, initTask));
            }
        }

        ITaskCluster res = getTaskManagerConfiguration().getTaskManagerWriter().saveNewGraphFromTaskCluster(taskCluster, taskObjectNodes);

        onTodoTasks(res, statusTasks);
        onCurrentTasks(res, statusTasks);

        return res;
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

    private void onTasks(ITaskCluster taskCluster, List<? extends ICommonTask> tasks, ExecuteTaskListener executeTaskListener) {
        if (tasks != null && !tasks.isEmpty()) {
            ITaskCycleListener[] ls = eventListenerList.getListeners(ITaskCycleListener.class);
            for (ICommonTask task : tasks) {
                ITaskDefinition taskDefinition = getTaskManagerConfiguration().getTaskDefinitionRegistry().getTaskDefinition(task.getCodeTaskDefinition());
                for (ITaskCycleListener l : ls) {
                    executeTaskListener.execute(l, taskCluster, taskDefinition, task);
                }
                ITaskService taskService = getTaskService(task);
                if (taskService != null) {
                    executeTaskListener.execute(taskService, taskCluster, taskDefinition, task);
                }
            }
        }
    }

    private void onTodoTasks(ITaskCluster taskCluster, List<? extends ICommonTask> tasks) {
        onTasks(taskCluster, tasks, TODO_EXECUTE_TASK_LISTENER);
    }

    private void onCurrentTasks(ITaskCluster taskCluster, List<? extends ICommonTask> tasks) {
        onTasks(taskCluster, tasks, CURRENT_EXECUTE_TASK_LISTENER);
    }

    private void onDoneTasks(ITaskCluster taskCluster, List<? extends ICommonTask> tasks) {
        onTasks(taskCluster, tasks, DONE_EXECUTE_TASK_LISTENER);
    }

    private void onNothingTasks(ITaskCluster taskCluster, List<? extends ICommonTask> tasks) {
        onTasks(taskCluster, tasks, NOTHING_EXECUTE_TASK_LISTENER);
    }

    private void onDeleteTasks(ITaskCluster taskCluster, List<ICommonTask> tasks) {
        onTasks(taskCluster, tasks, DELETE_EXECUTE_TASK_LISTENER);
    }

    // Inner class

    private interface ExecuteTaskListener {

        void execute(ITaskCycleListener taskCycleListener, ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task);

    }

    private static class TasksLists {

        List<ICommonTask> tasksToRemoves;

        List<ICommonTask> newCurrentTasks;

    }

    private static class MyEngineContext implements ITaskService.IEngineContext {

        private final ITaskCluster currentTaskCluster;

        private final ITaskDefinition taskDefinition;

        private boolean lock;

        private List<Pair<List<ITaskObject>, TaskClusterCallback>> startEngineTaskObjects = new ArrayList<>();

        private List<ITaskCluster> startEngineTaskClusters = new ArrayList<>();

        private List<Pair<ITaskCluster, List<ITaskObject>>> addToTaskClusters = new ArrayList<>();

        private List<ITaskObject> removeFromTaskClusters = new ArrayList<>();

        private List<Pair<List<ITaskObject>, TaskClusterCallback>> moveToNewTaskClusters = new ArrayList<>();

        private List<Pair<ITaskCluster, List<ITaskObject>>> moveToTaskClusters = new ArrayList<>();

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
        public void startEngine(ITaskObject... taskObjects) {
            startEngine(null, taskObjects);
        }

        @Override
        public void startEngine(TaskClusterCallback taskClusterCallback, ITaskObject... taskObjects) {
            verifyBlock();

            startEngineTaskObjects.add(Pair.of(Arrays.asList(taskObjects), taskClusterCallback));
        }

        @Override
        public void startEngine(ITaskCluster... taskClusters) {
            verifyBlock();

            startEngineTaskClusters.addAll(Arrays.asList(taskClusters));
        }

        @Override
        public void addTaskObjectsToTaskCluster(ITaskObject... taskObjects) {
            addTaskObjectsToTaskCluster(currentTaskCluster, taskObjects);
        }

        @Override
        public void addTaskObjectsToTaskCluster(ITaskCluster taskCluster, ITaskObject... taskObjects) {
            verifyBlock();

            addToTaskClusters.add(Pair.of(taskCluster, Arrays.asList(taskObjects)));
        }

        @Override
        public void removeTaskObjectsFromTaskCluster(ITaskObject... taskObjects) {
            verifyBlock();

            removeFromTaskClusters.addAll(Arrays.asList(taskObjects));
        }

        @Override
        public void moveTaskObjectsToNewTaskCluster(ITaskObject... taskObjects) {
            moveTaskObjectsToNewTaskCluster(null, taskObjects);
        }

        @Override
        public void moveTaskObjectsToNewTaskCluster(TaskClusterCallback taskClusterCallback, ITaskObject... taskObjects) {
            verifyBlock();

            moveToNewTaskClusters.add(Pair.of(Arrays.asList(taskObjects), taskClusterCallback));
        }

        @Override
        public void moveTaskObjectsToTaskCluster(ITaskObject... taskObjects) {
            moveTaskObjectsToTaskCluster(currentTaskCluster, taskObjects);
        }

        @Override
        public void moveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, ITaskObject... taskObjects) {
            verifyBlock();

            moveToTaskClusters.add(Pair.of(dstTaskCluster, Arrays.asList(taskObjects)));
        }
    }
}
