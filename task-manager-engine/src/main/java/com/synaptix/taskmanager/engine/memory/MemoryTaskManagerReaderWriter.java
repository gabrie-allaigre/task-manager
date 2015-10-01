package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.Map.Entry;

public class MemoryTaskManagerReaderWriter implements ITaskManagerReader, ITaskManagerWriter {

	private static final Log LOG = LogFactory.getLog(MemoryTaskManagerReaderWriter.class);

	private Map<ITaskCluster, List<ITaskObject>> taskClusterMap;

	private Map<ITaskCluster, List<ICommonTask>> currentTasksMap;

	public MemoryTaskManagerReaderWriter() {
		super();

		this.taskClusterMap = new HashMap<ITaskCluster, List<ITaskObject>>();
		this.currentTasksMap = new HashMap<ITaskCluster, List<ICommonTask>>();
	}

	/**
	 * Add array of task object in task cluster
	 *
	 * @param taskCluster a current cluster
	 * @param taskObjects task objects
	 */
	public void addTaskObjectsInTaskCluster(ITaskCluster taskCluster, ITaskObject... taskObjects) {
		taskClusterMap.get(taskCluster).addAll(Arrays.asList(taskObjects));
	}

	// WRITER

	@Override
	public ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster) {
		LOG.info("MRW - saveNewTaskClusterForTaskObject");
		taskClusterMap.put(taskCluster, new ArrayList<ITaskObject>());
		currentTasksMap.put(taskCluster, new ArrayList<ICommonTask>());
		return taskCluster;
	}

	@Override
	public ITaskCluster saveNewGraphFromTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject, IStatusTask>> taskObjectTasks) {
		LOG.info("MRW - saveNewTaskObjectInTaskCluster");

		if (taskObjectTasks != null && !taskObjectTasks.isEmpty()) {
			List<ITaskObject> tos = taskClusterMap.get(taskCluster);
			List<ICommonTask> ats = currentTasksMap.get(taskCluster);

			for (Pair<ITaskObject, IStatusTask> taskObjectNode : taskObjectTasks) {
				ITaskObject taskObject = taskObjectNode.getLeft();
				tos.add(taskObject);

				IStatusTask task = taskObjectNode.getRight();

				((AbstractSimpleCommonTask) task).setTaskObject(taskObject);
				((AbstractSimpleCommonTask) task).setStatus(AbstractSimpleCommonTask.Status.CURRENT);

				ats.add(task);
			}
		}

		((SimpleTaskCluster) taskCluster).setCheckGraphCreated(true);

		return taskCluster;
	}

	@Override
	public void saveRemoveTaskObjectsFromTaskCluster(ITaskCluster taskCluster, List<ITaskObject> taskObjects) {
		LOG.info("MRW - saveRemoveTaskObjectsFromTaskCluster");

		if (taskObjects != null && !taskObjects.isEmpty()) {
			List<ITaskObject> tos = taskClusterMap.get(taskCluster);
			List<ICommonTask> ats = currentTasksMap.get(taskCluster);

			for (ITaskObject taskObject : taskObjects) {
				tos.remove(taskObject);

				Iterator<ICommonTask> it = ats.iterator();
				while (it.hasNext()) {
					ICommonTask task = it.next();
					if (task instanceof AbstractSimpleCommonTask) {
						if (((AbstractSimpleCommonTask) task).getTaskObject().equals(taskObject)) {
							it.remove();
						}
					}
				}
			}
		}
	}

	@Override
	public ITaskCluster saveMoveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, Map<ITaskCluster, List<ITaskObject>> modifyClusterMap) {
		if (modifyClusterMap != null && !modifyClusterMap.isEmpty()) {
			List<ITaskObject> dstTos = taskClusterMap.get(dstTaskCluster);
			List<ICommonTask> dstAts = currentTasksMap.get(dstTaskCluster);

			for (Entry<ITaskCluster, List<ITaskObject>> entry : modifyClusterMap.entrySet()) {
				ITaskCluster srcTaskCluster = entry.getKey();
				List<ITaskObject> taskObjects = entry.getValue();

				if (taskObjects != null && !taskObjects.isEmpty()) {
					List<ITaskObject> srcTos = taskClusterMap.get(srcTaskCluster);
					List<ICommonTask> srcAts = currentTasksMap.get(srcTaskCluster);

					for (ITaskObject taskObject : taskObjects) {
						srcTos.remove(taskObject);
						dstTos.add(taskObject);

						Iterator<ICommonTask> it = srcAts.iterator();
						while (it.hasNext()) {
							ICommonTask task = it.next();
							if (task instanceof AbstractSimpleCommonTask) {
								if (((AbstractSimpleCommonTask) task).getTaskObject().equals(taskObject)) {
									it.remove();
									dstAts.add(task);
								}
							}
						}
					}
				}
			}
		}

		((SimpleTaskCluster) dstTaskCluster).setCheckGraphCreated(true);

		return dstTaskCluster;
	}

	@Override
	public ITaskCluster archiveTaskCluster(ITaskCluster taskCluster) {
		LOG.info("MRW - archiveTaskCluster " + taskCluster);
		((SimpleTaskCluster) taskCluster).setCheckArchived(true);
		return taskCluster;
	}

	@Override
	public void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, IStatusTask toDoneTask, Object taskServiceResult, List<ICommonTask> newTasks, Map<ISubTask, List<ICommonTask>> linkNextTasksMap,
			Map<IStatusTask, List<ICommonTask>> otherBranchFirstTasksMap, List<ICommonTask> nextCurrentTasks, List<ICommonTask> deleteTasks) {
		LOG.info("MRW - saveNewNextTasksInTaskCluster");

		((AbstractSimpleCommonTask) toDoneTask).setStatus(AbstractSimpleCommonTask.Status.DONE);

		currentTasksMap.get(taskCluster).remove(toDoneTask);

		if (deleteTasks != null && !deleteTasks.isEmpty()) {
			currentTasksMap.get(taskCluster).removeAll(deleteTasks);
		}

		ITaskObject taskObject = ((SimpleStatusTask) toDoneTask).getTaskObject();
		if (newTasks != null && !newTasks.isEmpty()) {
			for (ICommonTask task : newTasks) {
				if (task instanceof AbstractSimpleCommonTask) {
					((AbstractSimpleCommonTask) task).setTaskObject(taskObject);
					((AbstractSimpleCommonTask) task).setStatus(AbstractSimpleCommonTask.Status.TODO);
				}
				if (task instanceof SimpleSubTask) {
					SimpleSubTask simpleSubTask = (SimpleSubTask) task;
					List<ICommonTask> nextTasks = linkNextTasksMap.get(simpleSubTask);
					if (nextTasks != null && !nextTasks.isEmpty()) {
						simpleSubTask.getNextTasks().addAll(nextTasks);

						for (ICommonTask nextTask : nextTasks) {
							((AbstractSimpleCommonTask) nextTask).getPreviousTasks().add(task);
						}
					}
				}
				if (task instanceof SimpleStatusTask) {
					SimpleStatusTask simpleStatusTask = (SimpleStatusTask) task;
					List<ICommonTask> otherPreviousNextTasks = otherBranchFirstTasksMap.get(simpleStatusTask);
					if (otherPreviousNextTasks != null && !otherPreviousNextTasks.isEmpty()) {
						simpleStatusTask.getOtherBranchFirstTasks().addAll(otherPreviousNextTasks);
					}
				}
			}
		}

		if (nextCurrentTasks != null && !nextCurrentTasks.isEmpty()) {
			for (ICommonTask nextCurrentTask : nextCurrentTasks) {
				((AbstractSimpleCommonTask) nextCurrentTask).setStatus(AbstractSimpleCommonTask.Status.CURRENT);
			}
		}

		currentTasksMap.get(taskCluster).addAll(nextCurrentTasks);
	}

	@Override
	public void saveNextTasksInTaskCluster(ITaskCluster taskCluster, ICommonTask toDoneTask, Object taskServiceResult, List<ICommonTask> nextCurrentTasks) {
		LOG.info("MRW - saveNextTasksInTaskCluster");

		((AbstractSimpleCommonTask) toDoneTask).setStatus(AbstractSimpleCommonTask.Status.DONE);

		currentTasksMap.get(taskCluster).remove(toDoneTask);

		if (nextCurrentTasks != null && !nextCurrentTasks.isEmpty()) {
			for (ICommonTask nextCurrentTask : nextCurrentTasks) {
				AbstractSimpleCommonTask nct = (AbstractSimpleCommonTask) nextCurrentTask;
				nct.setStatus(AbstractSimpleCommonTask.Status.CURRENT);
			}
		}

		currentTasksMap.get(taskCluster).addAll(nextCurrentTasks);
	}

	@Override
	public void saveNothingTask(ITaskCluster taskCluster, ICommonTask nothingTask, Object taskServiceResult, Throwable errorMessage) {
		LOG.info("MRW - saveNothingTask");
	}

	// READER

	@Override
	public ITaskCluster findTaskClusterByTaskObject(ITaskObject taskObject) {
		LOG.info("MRW - findTaskClusterByTaskObject");

		for (Entry<ITaskCluster, List<ITaskObject>> entry : taskClusterMap.entrySet()) {
			if (entry.getValue().contains(taskObject)) {
				return entry.getKey();
			}
		}
		return null;
	}

	@Override
	public List<? extends ITaskObject> findTaskObjectsByTaskCluster(ITaskCluster taskCluster) {
		LOG.info("MRW - findTaskObjectsByTaskCluster");
		return taskClusterMap.get(taskCluster);
	}

	@Override
	public List<? extends ICommonTask> findCurrentTasksByTaskCluster(ITaskCluster taskCluster) {
		LOG.info("MRW - findCurrentTasksByTaskCluster");
		return currentTasksMap.get(taskCluster);
	}

	@Override
	public List<? extends ICommonTask> findNextTasksBySubTask(ISubTask subTask) {
		LOG.info("MRW - findNextTasksBySubTask");

		List<ICommonTask> res = new ArrayList<ICommonTask>();

		List<ICommonTask> nextTasks = ((SimpleSubTask) subTask).getNextTasks();
		if (nextTasks != null && !nextTasks.isEmpty()) {
			for (ICommonTask nextTask : nextTasks) {
				List<ICommonTask> previousTasks = ((AbstractSimpleCommonTask) nextTask).getPreviousTasks();
				boolean allFinish = true;
				if (previousTasks != null && !previousTasks.isEmpty()) {
					Iterator<ICommonTask> previousTaskIt = previousTasks.iterator();
					while (previousTaskIt.hasNext() && allFinish) {
						ICommonTask previousTask = previousTaskIt.next();
						if (!previousTask.equals(subTask) && (AbstractSimpleCommonTask.Status.TODO.equals(((AbstractSimpleCommonTask) nextTask).getStatus()) || AbstractSimpleCommonTask.Status.CURRENT
								.equals(((AbstractSimpleCommonTask) nextTask).getStatus()))) {
							allFinish = false;
						}
					}
				}
				if (allFinish) {
					res.add(nextTask);
				}
			}
		}

		return res;
	}

	@Override
	public List<? extends ICommonTask> findOtherBranchFirstTasksByStatusTask(IStatusTask statusTask) {
		LOG.info("MRW - findOtherBranchFirstTasksByStatusTask");
		return ((SimpleStatusTask) statusTask).getOtherBranchFirstTasks();
	}
}
