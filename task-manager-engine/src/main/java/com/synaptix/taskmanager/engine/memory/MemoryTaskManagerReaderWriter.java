package com.synaptix.taskmanager.engine.memory;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.synaptix.taskmanager.engine.task.AbstractTask;
import com.synaptix.taskmanager.engine.task.UpdateStatusTask;
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

	/**
	 * Add array of task object in task cluster
	 *
	 * @param taskCluster
	 * @param taskObjects
	 */
	public void addTaskObjectsInTaskCluster(ITaskCluster taskCluster, ITaskObject<?>... taskObjects) {
		taskClusterMap.get(taskCluster).addAll(Arrays.asList(taskObjects));
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

		if (taskObjectTasks != null && !taskObjectTasks.isEmpty()) {
			List<ITaskObject<?>> tos = taskClusterMap.get(taskCluster);
			List<AbstractTask> ats = taskNodeMap.get(taskCluster);

			for (Pair<ITaskObject<?>, UpdateStatusTask> taskObjectNode : taskObjectTasks) {
				ITaskObject<?> taskObject = taskObjectNode.getLeft();
				tos.add(taskObject);

				UpdateStatusTask task = taskObjectNode.getRight();

				((ISimpleCommon) task).setTaskObject(taskObject);

				ats.add(task);
			}
		}

		((SimpleTaskCluster) taskCluster).setCheckGraphCreated(true);

		return taskCluster;
	}

	@Override
	public void saveRemoveTaskObjectsForTaskCluster(ITaskCluster taskCluster, List<ITaskObject<?>> taskObjects) {
		LOG.info("MRW - saveRemoveTaskObjectsForTaskCluster");

		if (taskObjects != null && !taskObjects.isEmpty()) {
			List<ITaskObject<?>> tos = taskClusterMap.get(taskCluster);
			List<AbstractTask> ats = taskNodeMap.get(taskCluster);

			for (ITaskObject<?> taskObject : taskObjects) {
				tos.remove(taskObject);

				Iterator<AbstractTask> it = ats.iterator();
				while(it.hasNext()) {
					AbstractTask task = it.next();
					if (task instanceof ISimpleCommon) {
						if (((ISimpleCommon)task).getTaskObject().equals(taskObject)) {
							it.remove();
						}
					}
				}
			}
		}
	}

	@Override
	public ITaskCluster archiveTaskCluster(ITaskCluster taskCluster) {
		LOG.info("MRW - archiveTaskCluster " + taskCluster);
		((SimpleTaskCluster) taskCluster).setCheckArchived(true);
		return taskCluster;
	}

	@Override
	public void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, UpdateStatusTask toDoneTask, Object taskServiceResult, List<AbstractTask> newNextCurrentTasks, List<AbstractTask> deleteTasks) {
		LOG.info("MRW - saveNewNextTasksInTaskCluster");

		taskNodeMap.get(taskCluster).remove(toDoneTask);
		taskNodeMap.get(taskCluster).removeAll(deleteTasks);

		update(((SimpleUpdateStatusTask) toDoneTask).getTaskObject(), newNextCurrentTasks);
		taskNodeMap.get(taskCluster).addAll(newNextCurrentTasks);
	}

	private void update(ITaskObject<?> taskObject, List<AbstractTask> tasks) {
		if (tasks != null && !tasks.isEmpty()) {
			for (AbstractTask task : tasks) {
				if (task instanceof SimpleUpdateStatusTask) {
					((SimpleUpdateStatusTask) task).setTaskObject(taskObject);
				} else if (task instanceof SimpleNormalTask) {
					SimpleNormalTask normalTask = (SimpleNormalTask) task;
					normalTask.setTaskObject(taskObject);
					update(taskObject, normalTask.getNextTasks());
				}
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
