package com.synaptix.taskmanager.engine.configuration.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.task.UpdateStatusTask;
import com.synaptix.taskmanager.model.ITaskObject;

public class StatusGraphRegistryBuilder {

	private final MyStatusGraphRegistry statusGraphRegistry;

	private StatusGraphRegistryBuilder() {
		super();

		this.statusGraphRegistry = new MyStatusGraphRegistry();
	}

	public <E extends Object, F extends ITaskObject<E>> StatusGraphRegistryBuilder addStatusGraphs(Class<F> taskObjectClass, List<IStatusGraph<E>> statusGraphs) {
		statusGraphRegistry.statusGraphMap.put(taskObjectClass, statusGraphs);
		return this;
	}

	public IStatusGraphRegistry build() {
		return statusGraphRegistry;
	}

	public static StatusGraphRegistryBuilder newBuilder() {
		return new StatusGraphRegistryBuilder();
	}

	private static final class MyStatusGraphRegistry extends AbstractStatusGraphRegistry {

		private Map<Class<?>, List<? extends IStatusGraph<?>>> statusGraphMap;

		public MyStatusGraphRegistry() {
			super();

			this.statusGraphMap = new HashMap<Class<?>, List<? extends IStatusGraph<?>>>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <E, F extends ITaskObject<E>> List<IStatusGraph<E>> getNextStatusGraphsByTaskObjectType(Class<F> taskObjectClass, UpdateStatusTask updateStatusTask, E currentStatus) {
			List<IStatusGraph<E>> res = new ArrayList<IStatusGraph<E>>();
			List<IStatusGraph<E>> statusGraphs = (List<IStatusGraph<E>>) statusGraphMap.get(taskObjectClass);
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
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}