package com.synaptix.taskmanager.engine.graph;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatusGraphsBuilder<F> {

	private final F previousStatus;

	private List<Pair<F, String>> pairs;

	private List<IStatusGraph<F>> statusGraphs;

	protected StatusGraphsBuilder(F previousStatus) {
		super();

		this.previousStatus = previousStatus;

		this.pairs = new ArrayList<>();
		this.statusGraphs = new ArrayList<>();
	}

	public StatusGraphsBuilder<F> addNextStatusGraph(F nextStatus, String nextStatusTaskServiceCode) {
		return addNextStatusGraph(nextStatus,nextStatusTaskServiceCode,null);
	}

	public StatusGraphsBuilder<F> addNextStatusGraph(F nextStatus, String nextStatusTaskServiceCode, StatusGraphsBuilder<F> statusGraphsBuilder) {
		this.pairs.add(Pair.of(nextStatus, nextStatusTaskServiceCode));
		if (statusGraphsBuilder.pairs != null && !statusGraphsBuilder.pairs.isEmpty()) {
			this.statusGraphs.addAll(statusGraphsBuilder.pairs.stream().map(pair -> new MyStatusGraph<>(nextStatus, pair.getLeft(), pair.getRight())).collect(Collectors.toList()));
			this.statusGraphs.addAll(statusGraphsBuilder.statusGraphs);
		}
		return this;
	}

	public List<IStatusGraph<F>> build() {
		List<IStatusGraph<F>> res = pairs.stream().map(pair -> new MyStatusGraph<>(previousStatus, pair.getLeft(), pair.getRight())).collect(Collectors.toList());
		res.addAll(statusGraphs);
		return res;
	}

	public static <F> StatusGraphsBuilder<F> newBuilder() {
		return newBuilder(null);
	}

	public static <F> StatusGraphsBuilder<F> newBuilder(F previousStatus) {
		return new StatusGraphsBuilder<>(previousStatus);
	}

	private static class MyStatusGraph<F> implements IStatusGraph<F> {

		private final F previousStatus;

		private final F currentStatus;

		private final String statusTaskServiceCode;

		public MyStatusGraph(F previousStatus, F currentStatus, String statusTaskServiceCode) {
			super();

			this.previousStatus = previousStatus;
			this.currentStatus = currentStatus;
			this.statusTaskServiceCode = statusTaskServiceCode;
		}

		@Override
		public F getPreviousStatus() {
			return previousStatus;
		}

		@Override
		public F getCurrentStatus() {
			return currentStatus;
		}

		@Override
		public String getStatusTaskServiceCode() {
			return this.statusTaskServiceCode;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
