package com.synaptix.taskmanager.engine.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;

public class StatusGraphsBuilder {

	private final Object previousStatus;

	private List<Pair<Object, String>> pairs;

	private List<IStatusGraph> statusGraphs;

	protected StatusGraphsBuilder() {
		this(null);
	}

	protected StatusGraphsBuilder(Object previousStatus) {
		super();

		this.previousStatus = previousStatus;

		this.pairs = new ArrayList<Pair<Object, String>>();
		this.statusGraphs = new ArrayList<IStatusGraph>();
	}

	public StatusGraphsBuilder addNextStatusGraph(Object nextStatus, String nextUpdateStatusTaskServiceCode) {
		this.pairs.add(Pair.of(nextStatus, nextUpdateStatusTaskServiceCode));
		return this;
	}

	public StatusGraphsBuilder addNextStatusGraph(Object nextStatus, String nextUpdateStatusTaskServiceCode, StatusGraphsBuilder statusGraphsBuilder) {
		this.pairs.add(Pair.of(nextStatus, nextUpdateStatusTaskServiceCode));
		if (statusGraphsBuilder.pairs != null && !statusGraphsBuilder.pairs.isEmpty()) {
			for (Pair<Object, String> pair : statusGraphsBuilder.pairs) {
				this.statusGraphs.add(new MyStatusGraph(nextStatus, pair.getLeft(), pair.getRight()));
			}
			this.statusGraphs.addAll(statusGraphsBuilder.statusGraphs);
		}
		return this;
	}

	public List<IStatusGraph> build() {
		List<IStatusGraph> res = new ArrayList<IStatusGraph>();
		for (Pair<Object, String> pair : pairs) {
			res.add(new MyStatusGraph(previousStatus, pair.getLeft(), pair.getRight()));
		}
		res.addAll(statusGraphs);
		return res;
	}

	public static StatusGraphsBuilder newBuilder() {
		return new StatusGraphsBuilder();
	}

	public static StatusGraphsBuilder newBuilder(Object previousStatus) {
		return new StatusGraphsBuilder(previousStatus);
	}

	private static class MyStatusGraph implements IStatusGraph {

		private Object previousStatus;

		private Object currentStatus;

		private String updateStatusTaskServiceCode;

		public MyStatusGraph(Object previousStatus, Object currentStatus, String updateStatusTaskServiceCode) {
			super();

			this.previousStatus = previousStatus;
			this.currentStatus = currentStatus;
			this.updateStatusTaskServiceCode = updateStatusTaskServiceCode;
		}

		@Override
		public Object getPreviousStatus() {
			return previousStatus;
		}

		@Override
		public Object getCurrentStatus() {
			return currentStatus;
		}

		@Override
		public String getUpdateStatusTaskServiceCode() {
			return this.updateStatusTaskServiceCode;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
