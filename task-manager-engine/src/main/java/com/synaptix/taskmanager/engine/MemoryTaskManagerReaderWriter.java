package com.synaptix.taskmanager.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public class MemoryTaskManagerReaderWriter implements ITaskManagerReader, ITaskManagerWriter {

	private static final Log LOG = LogFactory.getLog(MemoryTaskManagerReaderWriter.class);

	private Map<ITaskCluster, List<ITaskObject<?>>> taskClusterMap;

	public MemoryTaskManagerReaderWriter() {
		super();

		this.taskClusterMap = new HashMap<ITaskCluster, List<ITaskObject<?>>>();
	}

	@Override
	public ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster) {
		LOG.info("saveNewTaskClusterForTaskObject " + taskCluster);
		taskClusterMap.put(taskCluster, new ArrayList<ITaskObject<?>>());
		return taskCluster;
	}

	@Override
	public List<ITask> saveNewTaskObjectInTaskCluster(ITaskCluster taskCluster, ITaskObject<?> taskObject, TaskNode taskNode) {
		LOG.info("saveNewTaskObjectInTaskCluster " + taskCluster);
		taskClusterMap.get(taskCluster).add(taskObject);
		return new ArrayList<ITask>();
	}

	@Override
	public void archiveCluster(ITaskCluster taskCluster) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveTask(ITask task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteTasksTodo(ITask task) {
		// TODO Auto-generated method stub

	}

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
	public List<ITask> findCurrentTasksForCluster(ITaskCluster taskCluster) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ITask> selectNextTodoToCurrentTasks(ITask task) {
		// TODO Auto-generated method stub
		return null;
	}

}
