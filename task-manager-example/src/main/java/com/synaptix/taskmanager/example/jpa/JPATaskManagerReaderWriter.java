package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.example.jpa.model.Cluster;
import com.synaptix.taskmanager.example.jpa.model.Todo;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JPATaskManagerReaderWriter implements ITaskManagerReader, ITaskManagerWriter {

	private static final Log LOG = LogFactory.getLog(JPATaskManagerReaderWriter.class);

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

		return null;
	}

	@Override
	public List<? extends ICommonTask> findNextTasksBySubTask(ISubTask subTask) {
		LOG.info("JPARW - findNextTasksBySubTask");

		return null;
	}

	@Override
	public List<? extends ICommonTask> findOtherBranchFirstTasksByStatusTask(IStatusTask statusTask) {
		return null;
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

				IStatusTask task = taskObjectNode.getRight();
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
	}

	@Override
	public void saveNothingTask(ITaskCluster taskCluster, ICommonTask nothingTask, Object taskServiceResult, Throwable errorMessage) {
		LOG.info("JPARW - saveNothingTask");
	}

	@Override
	public void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, IStatusTask toDoneTask, Object taskServiceResult, List<ICommonTask> newTasks,
			Map<ISubTask, List<ICommonTask>> linkNextTasksMap, Map<IStatusTask, List<ICommonTask>> otherBranchFirstTasksMap, List<ICommonTask> nextCurrentTasks,
			List<ICommonTask> deleteTasks) {
		LOG.info("JPARW - saveNewNextTasksInTaskCluster");
	}
}
