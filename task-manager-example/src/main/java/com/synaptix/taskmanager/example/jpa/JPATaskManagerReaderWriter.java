package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.example.jpa.model.Cluster;
import com.synaptix.taskmanager.example.jpa.model.Task;
import com.synaptix.taskmanager.example.jpa.model.Todo;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JPATaskManagerReaderWriter implements ITaskManagerReader, ITaskManagerWriter {

	private static final Log LOG = LogFactory.getLog(JPATaskManagerReaderWriter.class);

	private final ITaskDefinitionRegistry taskDefinitionRegistry;

	public JPATaskManagerReaderWriter(ITaskDefinitionRegistry taskDefinitionRegistry) {
		super();

		this.taskDefinitionRegistry = taskDefinitionRegistry;
	}

	// WRITER

	@Override
	public ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster) {
		LOG.info("JPARW - saveNewTaskCluster");

		Cluster cluster = (Cluster) taskCluster;

		JPAHelper.getInstance().getEntityManager().persist(cluster);

		return cluster;
	}

	@Override
	public ITaskCluster saveNewGraphFromTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject, IStatusTask>> taskObjectTasks) {
		LOG.info("JPARW - saveNewTaskClusterForTaskObject");

		JPAHelper.getInstance().getEntityManager().getTransaction().begin();

		Cluster cluster = (Cluster) taskCluster;

		if (taskObjectTasks != null && !taskObjectTasks.isEmpty()) {
			for (Pair<ITaskObject, IStatusTask> taskObjectNode : taskObjectTasks) {
				Todo todo = (Todo) taskObjectNode.getLeft();
				todo.setCluster(cluster);

				List<Todo> todos = cluster.getTodos();
				if (todos == null) {
					todos = new ArrayList<Todo>();
					cluster.setTodos(todos);
				}
				todos.add(todo);

				Task statusTask = (Task) taskObjectNode.getRight();
				statusTask.setStatus("CURRENT");
				statusTask.setCluster(cluster);
				statusTask.setTodo(todo);
				JPAHelper.getInstance().getEntityManager().persist(statusTask);
			}
		}

		cluster.setCheckGraphCreated(true);
		JPAHelper.getInstance().getEntityManager().persist(cluster);

		JPAHelper.getInstance().getEntityManager().getTransaction().commit();

		return cluster;
	}

	@Override
	public void saveRemoveTaskObjectsFromTaskCluster(ITaskCluster taskCluster, List<ITaskObject> taskObjects) {
		LOG.info("JPARW - saveRemoveTaskObjectsFromTaskCluster");
	}

	@Override
	public ITaskCluster saveMoveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, Map<ITaskCluster, List<ITaskObject>> modifyClusterMap, boolean newTaskCluster) {
		LOG.info("JPARW - saveMoveTaskObjectsToTaskCluster");
		return null;
	}

	@Override
	public ITaskCluster archiveTaskCluster(ITaskCluster taskCluster) {
		LOG.info("JPARW - archiveTaskCluster");

		Cluster cluster = (Cluster) taskCluster;
		cluster.setCheckArchived(true);
		JPAHelper.getInstance().getEntityManager().persist(cluster);

		return cluster;
	}

	@Override
	public void saveNextTasksInTaskCluster(ITaskCluster taskCluster, ICommonTask toDoneTask, Object taskServiceResult, List<ICommonTask> nextCurrentTasks) {
		LOG.info("JPARW - saveNextTasksInTaskCluster");

		JPAHelper.getInstance().getEntityManager().getTransaction().begin();

		Cluster cluster = (Cluster) taskCluster;

		Task tdt = (Task)toDoneTask;
		tdt.setStatus("DONE");
		JPAHelper.getInstance().getEntityManager().persist(tdt);

		if (nextCurrentTasks != null && !nextCurrentTasks.isEmpty()) {
			for(ICommonTask nextCurrentTask : nextCurrentTasks) {
				Task nct = (Task)nextCurrentTask;
				nct.setStatus("CURRENT");
				JPAHelper.getInstance().getEntityManager().persist(nct);
			}
		}

		JPAHelper.getInstance().getEntityManager().getTransaction().commit();
	}

	@Override
	public void saveNothingTask(ITaskCluster taskCluster, ICommonTask nothingTask, Object taskServiceResult, Throwable errorMessage) {
		LOG.info("JPARW - saveNothingTask");
	}

	@Override
	public void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, IStatusTask toDoneTask, Object taskServiceResult, List<ICommonTask> newTasks, Map<ISubTask, List<ICommonTask>> linkNextTasksMap,
			Map<IStatusTask, List<ICommonTask>> otherBranchFirstTasksMap, List<ICommonTask> nextCurrentTasks, List<ICommonTask> deleteTasks) {
		LOG.info("JPARW - saveNewNextTasksInTaskCluster");

		JPAHelper.getInstance().getEntityManager().getTransaction().begin();

		Cluster cluster = (Cluster) taskCluster;
		Task tdt = (Task)toDoneTask;
		tdt.setStatus("DONE");
		JPAHelper.getInstance().getEntityManager().persist(tdt);

		if (newTasks != null && !newTasks.isEmpty()) {
			for(ICommonTask newTask : newTasks) {
				Task nct = (Task)newTask;
				nct.setStatus("TODO");
				nct.setCluster(cluster);
				nct.setTodo(tdt.getTodo());

				JPAHelper.getInstance().getEntityManager().persist(nct);
			}
		}

		if (linkNextTasksMap != null && !linkNextTasksMap.isEmpty()) {
			for(Map.Entry<ISubTask, List<ICommonTask>> entry : linkNextTasksMap.entrySet()) {
				Task nct = (Task)entry.getKey();

				List<Task> childs = new ArrayList<Task>();
				List<ICommonTask> ts = entry.getValue();
				if (ts != null && !ts.isEmpty()) {
					for(ICommonTask t : ts) {
						childs.add((Task)t);
					}
				}
				nct.setNextTasks(childs);

				JPAHelper.getInstance().getEntityManager().persist(nct);
			}
		}

		if (otherBranchFirstTasksMap != null && !otherBranchFirstTasksMap.isEmpty()) {
			for(Map.Entry<IStatusTask, List<ICommonTask>> entry : otherBranchFirstTasksMap.entrySet()) {
				Task nct = (Task)entry.getKey();

				List<Task> childs = new ArrayList<Task>();
				List<ICommonTask> ts = entry.getValue();
				if (ts != null && !ts.isEmpty()) {
					for(ICommonTask t : ts) {
						childs.add((Task)t);
					}
				}
				nct.setOtherBranchFirstTasks(childs);

				JPAHelper.getInstance().getEntityManager().persist(nct);
			}
		}

		if (nextCurrentTasks != null && !nextCurrentTasks.isEmpty()) {
			List<Task> childs = new ArrayList<Task>();
			for (ICommonTask nextCurrentTask : nextCurrentTasks) {
				Task nct = (Task) nextCurrentTask;
				nct.setStatus("CURRENT");

				JPAHelper.getInstance().getEntityManager().persist(nct);

				childs.add(nct);
			}

			tdt.setNextTasks(childs);
			JPAHelper.getInstance().getEntityManager().persist(tdt);
		}

		if (deleteTasks != null && !deleteTasks.isEmpty()) {
			for (ICommonTask deleteTask : deleteTasks) {
				Task nct = (Task) deleteTask;
				nct.setStatus("DELETE");

				JPAHelper.getInstance().getEntityManager().persist(nct);
			}
		}

		JPAHelper.getInstance().getEntityManager().getTransaction().commit();
	}

	// READER

	@Override
	public ITaskCluster findTaskClusterByTaskObject(ITaskObject taskObject) {
		LOG.info("JPARW - findTaskClusterByTaskObject");
		Todo todo = (Todo) taskObject;
		return todo.getCluster();
	}

	@Override
	public List<? extends ITaskObject> findTaskObjectsByTaskCluster(ITaskCluster taskCluster) {
		LOG.info("JPARW - findTaskObjectsByTaskCluster");
		return ((Cluster) taskCluster).getTodos();
	}

	@Override
	public List<? extends ICommonTask> findCurrentTasksByTaskCluster(ITaskCluster taskCluster) {
		LOG.info("JPARW - findCurrentTasksByTaskCluster");

		Cluster cluster = (Cluster) taskCluster;

		Query q = JPAHelper.getInstance().getEntityManager().createQuery("select t from Task t where t.status = 'CURRENT' and t.cluster.id = " + cluster.getId());
		return q.getResultList();
	}

	@Override
	public List<? extends ICommonTask> findNextTasksBySubTask(ISubTask subTask) {
		LOG.info("JPARW - findNextTasksBySubTask");
		return ((Task)subTask).getNextTasks();
	}

	@Override
	public List<? extends ICommonTask> findOtherBranchFirstTasksByStatusTask(IStatusTask statusTask) {
		return ((Task)statusTask).getOtherBranchFirstTasks();
	}
}
