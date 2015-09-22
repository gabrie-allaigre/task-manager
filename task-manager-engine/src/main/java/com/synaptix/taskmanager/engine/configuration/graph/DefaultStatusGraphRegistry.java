package com.synaptix.taskmanager.engine.configuration.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.taskmanager.manager.graph.IStatusGraph;

public class DefaultStatusGraphRegistry extends AbstractStatusGraphRegistry {

	private Map<Class<?>, List<? extends IStatusGraph>> statusGraphMap;

	public DefaultStatusGraphRegistry() {
		super();

		this.statusGraphMap = new HashMap<Class<?>, List<? extends IStatusGraph>>();
	}

	public void addStatusGraphs(Class<?> taskObjectClass, List<IStatusGraph> statusGraphs) {
		this.statusGraphMap.put(taskObjectClass, statusGraphs);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IStatusGraph> getNextStatusGraphsByTaskObjectType(Class<?> taskObjectClass, Object currentStatus) {
		List<IStatusGraph> res = new ArrayList<IStatusGraph>();
		List<IStatusGraph> statusGraphs = (List<IStatusGraph>) statusGraphMap.get(taskObjectClass);
		if (statusGraphs != null && !statusGraphs.isEmpty()) {
			for (IStatusGraph statusGraph : statusGraphs) {
				if ((currentStatus == null && statusGraph.getPreviousStatus() == null) || (currentStatus != null && currentStatus.equals(statusGraph.getPreviousStatus()))) {
					res.add(statusGraph);
				}
			}
		}
		return res;
	}
}
