package com.synaptix.taskmanager.engine.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;

public class StatusGraphsBuilder<F extends Object> {

	private final F previousStatus;

	private List<Pair<F, String>> pairs;

	private List<IStatusGraph<F>> statusGraphs;

	protected StatusGraphsBuilder() {
		this(null);
	}

	protected StatusGraphsBuilder(F previousStatus) {
		super();

		this.previousStatus = previousStatus;

		this.pairs = new ArrayList<Pair<F, String>>();
		this.statusGraphs = new ArrayList<IStatusGraph<F>>();
	}

	public StatusGraphsBuilder<F> addNextStatusGraph(F nextStatus, String nextStatusTaskServiceCode) {
		this.pairs.add(Pair.of(nextStatus, nextStatusTaskServiceCode));
		return this;
	}

	public StatusGraphsBuilder<F> addNextStatusGraph(F nextStatus, String nextStatusTaskServiceCode, StatusGraphsBuilder<F> statusGraphsBuilder) {
		this.pairs.add(Pair.of(nextStatus, nextStatusTaskServiceCode));
		if (statusGraphsBuilder.pairs != null && !statusGraphsBuilder.pairs.isEmpty()) {
			for (Pair<F, String> pair : statusGraphsBuilder.pairs) {
				this.statusGraphs.add(new MyStatusGraph<F>(nextStatus, pair.getLeft(), pair.getRight()));
			}
			this.statusGraphs.addAll(statusGraphsBuilder.statusGraphs);
		}
		return this;
	}

	public List<IStatusGraph<F>> build() {
		List<IStatusGraph<F>> res = new ArrayList<IStatusGraph<F>>();
		for (Pair<F, String> pair : pairs) {
			res.add(new MyStatusGraph<F>(previousStatus, pair.getLeft(), pair.getRight()));
		}
		res.addAll(statusGraphs);
		return res;
	}

	public static <F extends Object> StatusGraphsBuilder<F> newBuilder() {
		return new StatusGraphsBuilder<F>();
	}

	public static <F extends Object> StatusGraphsBuilder<F> newBuilder(F previousStatus) {
		return new StatusGraphsBuilder<F>(previousStatus);
	}

	private static class MyStatusGraph<F extends Object> implements IStatusGraph<F> {

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
