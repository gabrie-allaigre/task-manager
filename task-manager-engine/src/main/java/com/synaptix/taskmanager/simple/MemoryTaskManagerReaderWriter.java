package com.synaptix.taskmanager.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.synaptix.taskmanager.manager.AbstractTask;
import com.synaptix.taskmanager.manager.UpdateStatusTask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public class MemoryTaskManagerReaderWriter implements ITaskManagerReader, ITaskManagerWriter {

	private static final Log LOG = LogFactory.getLog(MemoryTaskManagerReaderWriter.class);

	private Map<ITaskCluster, List<ITaskObject<?>>> taskClusterMap;

	private Map<ITaskCluster, List<AbstractTask>> taskNodeMap;

	public MemoryTaskManagerReaderWriter() {
		super();

		this.taskClusterMap = new HashMap<ITaskCluster, List<ITaskObject<?>>>();
		this.taskNodeMap = new HashMap<ITaskCluster, List<AbstractTask>>();
	}

	// WRITER

	@Override
	public ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster) {
		LOG.info("MRW - saveNewTaskClusterForTaskObject");
		taskClusterMap.put(taskCluster, new ArrayList<ITaskObject<?>>());
		taskNodeMap.put(taskCluster, new ArrayList<AbstractTask>());
		return taskCluster;
	}

	@Override
	public ITaskCluster saveNewGraphForTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject<?>, UpdateStatusTask>> taskObjectTasks) {
		LOG.info("MRW - saveNewTaskObjectInTaskCluster");

		for (Pair<ITaskObject<?>, UpdateStatusTask> taskObjectNode : taskObjectTasks) {
			ITaskObject<?> taskObject = taskObjectNode.getLeft();
			taskClusterMap.get(taskCluster).add(taskObject);

			UpdateStatusTask task = taskObjectNode.getRight();

			((SimpleUpdateStatusTask) task).setTaskObject(taskObject);

			taskNodeMap.get(taskCluster).add(task);
		}

		return taskCluster;
	}

	@Override
	public ITaskCluster archiveTaskCluster(ITaskCluster taskCluster) {
		LOG.info("MRW - archiveTaskCluster " + taskCluster);
		((SimpleTaskCluster) taskCluster).setCheckArchived(true);
		return taskCluster;
	}

	@Override
	public void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, UpdateStatusTask toDoneTask, Object taskServiceResult, List<AbstractTask> newNextCurrentTasks) {
		LOG.info("MRW - saveNewNextTasksInTaskCluster");

		taskNodeMap.get(taskCluster).remove(toDoneTask);

		update(((SimpleUpdateStatusTask) toDoneTask).getTaskObject(), newNextCurrentTasks);
		taskNodeMap.get(taskCluster).addAll(newNextCurrentTasks);
	}

	private void update(ITaskObject<?> taskObject, List<AbstractTask> tasks) {
		if (tasks != null && !tasks.isEmpty()) {
			for (AbstractTask task : tasks) {
				if (task instanceof SimpleUpdateStatusTask) {
					((SimpleUpdateStatusTask) task).setTaskObject(taskObject);
				} else if (task instanceof SimpleNormalTask) {
					((SimpleNormalTask) task).setTaskObject(taskObject);
				}
				update(taskObject, task.getNextTasks());
			}
		}
	}

	@Override
	public void saveNextTasksInTaskCluster(ITaskCluster taskCluster, AbstractTask toDoneTask, Object taskServiceResult, List<AbstractTask> nextCurrentTasks) {
		LOG.info("MRW - saveNextTasksInTaskCluster");

		taskNodeMap.get(taskCluster).remove(toDoneTask);
		taskNodeMap.get(taskCluster).addAll(nextCurrentTasks);
	}

	@Override
	public void saveNothingTask(ITaskCluster taskCluster, AbstractTask nothingTask, Object taskServiceResult, Throwable errorMessage) {
		LOG.info("MRW - saveNothingTask");
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
	public List<AbstractTask> findCurrentTasksByTaskCluster(ITaskCluster taskCluster) {
		return taskNodeMap.get(taskCluster);
	}
}
