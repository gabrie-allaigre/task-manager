package com.synaptix.taskmanager.engine.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;

import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.model.ITaskObject;

public class TaskObjectManagerBuilder<E extends Object, F extends ITaskObject> {

	private MyTaskObjectManager<E, F> taskObjectManager;

	private TaskObjectManagerBuilder(Class<F> taskObjectClass) {
		super();

		this.taskObjectManager = new MyTaskObjectManager<E, F>(taskObjectClass);
	}

	public TaskObjectManagerBuilder<E, F> initialStatus(E defaultInitialStatus) {
		taskObjectManager.defaultInitialStatus = defaultInitialStatus;
		return this;
	}

	public TaskObjectManagerBuilder<E, F> initialStatus(IGetStatus<E,F> getStatus) {
		taskObjectManager.getStatus = getStatus;
		return this;
	}

	public TaskObjectManagerBuilder<E, F> statusGraphs(List<IStatusGraph<E>> statusGraphs) {
		taskObjectManager.statusGraphs =statusGraphs;
		return this;
	}

	public TaskObjectManagerBuilder<E, F> addTaskChainCriteria(E currentStatus, E nextStatus, String taskChainCriteria) {
		taskObjectManager.taskChainCriteriaMap.put(Pair.of(currentStatus, nextStatus), taskChainCriteria);
		return this;
	}

	public ITaskObjectManager<E,F> build() {
		return taskObjectManager;
	}

	public static <E extends Object, F extends ITaskObject> TaskObjectManagerBuilder<E, F> newBuilder(Class<F> taskObjectClass) {
		return new TaskObjectManagerBuilder<E, F>(taskObjectClass);
	}

	public interface IGetStatus<E extends Object,F extends ITaskObject> {

		E getStatus(F taskObject);

	}

	private static class MyTaskObjectManager<E extends Object, F extends ITaskObject> extends AbstractTaskObjectManager<E,F> {

		private final Map<Pair<E, E>, String> taskChainCriteriaMap;

		private List<IStatusGraph<E>> statusGraphs;

		private E defaultInitialStatus;

		private IGetStatus<E,F> getStatus;

		public MyTaskObjectManager(Class<F> taskObjectClass) {
			super(taskObjectClass);

			this.taskChainCriteriaMap = new HashMap<Pair<E, E>, String>();
		}

		@Override
		public E getInitialStatus(F taskObject) {
			if (getStatus != null) {
				return getStatus.getStatus(taskObject);
			}
			return defaultInitialStatus;
		}

		@Override
		public List<IStatusGraph<E>> getNextStatusGraphsByTaskObjectType(IStatusTask statusTask, E currentStatus) {
			List<IStatusGraph<E>> res = new ArrayList<IStatusGraph<E>>();
			if (statusGraphs != null && !statusGraphs.isEmpty()) {
				for (IStatusGraph<E> statusGraph : statusGraphs) {
					if ((currentStatus == null && statusGraph.getPreviousStatus() == null) || (currentStatus != null && currentStatus.equals(statusGraph.getPreviousStatus()))) {
						res.add(statusGraph);
					}
				}
			}
			return res;
		}

		@Override
		public String getTaskChainCriteria(IStatusTask statusTask, Object currentStatus, Object nextStatus) {
			return taskChainCriteriaMap.get(Pair.of(currentStatus, nextStatus));
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
