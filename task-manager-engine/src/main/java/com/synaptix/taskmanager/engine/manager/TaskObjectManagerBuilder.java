package com.synaptix.taskmanager.engine.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;

import com.synaptix.taskmanager.engine.task.UpdateStatusTask;
import com.synaptix.taskmanager.model.ITaskObject;

public class TaskObjectManagerBuilder<E extends Object, F extends ITaskObject<E>> {

	private MyTaskObjectManager<E, F> taskObjectManager;

	private TaskObjectManagerBuilder(Class<F> taskObjectClass) {
		super();

		this.taskObjectManager = new MyTaskObjectManager<E, F>(taskObjectClass);
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

	public static <E extends Object, F extends ITaskObject<E>> TaskObjectManagerBuilder<E, F> newBuilder(Class<F> taskObjectClass) {
		return new TaskObjectManagerBuilder<E, F>(taskObjectClass);
	}

	private static class MyTaskObjectManager<E extends Object, F extends ITaskObject<E>> extends AbstractTaskObjectManager<E,F> {

		private List<IStatusGraph<E>> statusGraphs;

		private final Map<Pair<E, E>, String> taskChainCriteriaMap;

		public MyTaskObjectManager(Class<F> taskObjectClass) {
			super(taskObjectClass);

			this.taskChainCriteriaMap = new HashMap<Pair<E, E>, String>();
		}

		@Override
		public List<IStatusGraph<E>> getNextStatusGraphsByTaskObjectType(UpdateStatusTask updateStatusTask, E currentStatus) {
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
		public String getTaskChainCriteria(UpdateStatusTask updateStatusTask, Object currentStatus, Object nextStatus) {
			return taskChainCriteriaMap.get(Pair.of(currentStatus, nextStatus));
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
