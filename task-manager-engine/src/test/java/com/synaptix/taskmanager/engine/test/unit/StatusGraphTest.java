package com.synaptix.taskmanager.engine.test.unit;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

import com.synaptix.taskmanager.engine.configuration.graph.IStatusGraphRegistry;
import com.synaptix.taskmanager.engine.configuration.graph.StatusGraphRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.test.data.BusinessObject;

public class StatusGraphTest {

	/**
	 * null -> A
	 */
	@Test
	public void test1() {
		List<IStatusGraph<String>> statusGraphs = StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask").build();

		Assert.assertNotNull(statusGraphs);
		Assert.assertEquals(statusGraphs.size(), 1);

		assertUniqueContains(statusGraphs, null, "A", "ATask");
	}

	/**
	 * DEBUT -> A
	 */
	@Test
	public void test2() {
		List<IStatusGraph<String>> statusGraphs = StatusGraphsBuilder.<String> newBuilder("DEBUT").addNextStatusGraph("A", "ATask").build();

		Assert.assertNotNull(statusGraphs);
		Assert.assertEquals(statusGraphs.size(), 1);

		assertUniqueContains(statusGraphs, "DEBUT", "A", "ATask");
	}

	/**
	 * null -> (A,B)
	 */
	@Test
	public void test3() {
		List<IStatusGraph<String>> statusGraphs = StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask").addNextStatusGraph("B", "BTask").build();

		Assert.assertNotNull(statusGraphs);
		Assert.assertEquals(statusGraphs.size(), 2);

		assertUniqueContains(statusGraphs, null, "A", "ATask");
		assertUniqueContains(statusGraphs, null, "B", "BTask");
	}

	/**
	 * null -> (A -> C,B)
	 */
	@Test
	public void test4() {
		List<IStatusGraph<String>> statusGraphs = StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("C", "CTask"))
				.addNextStatusGraph("B", "BTask").build();

		Assert.assertNotNull(statusGraphs);
		Assert.assertEquals(statusGraphs.size(), 3);

		assertUniqueContains(statusGraphs, null, "A", "ATask");
		assertUniqueContains(statusGraphs, "A", "C", "CTask");
		assertUniqueContains(statusGraphs, null, "B", "BTask");
	}

	/**
	 * null -> (A -> C -> (A,D),B)
	 */
	@Test
	public void test5() {
		IStatusGraphRegistry statusGraphRegistry = StatusGraphRegistryBuilder.newBuilder()
				.addStatusGraphs(BusinessObject.class,
						StatusGraphsBuilder.<String> newBuilder()
								.addNextStatusGraph("A", "ATask",
										StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("C", "CTask",
												StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask").addNextStatusGraph("D", "DTask")))
								.addNextStatusGraph("B", "BTask").build())
				.build();

		List<IStatusGraph<String>> statusGraphs = statusGraphRegistry.getNextStatusGraphsByTaskObjectType(BusinessObject.class, null, null);
		Assert.assertNotNull(statusGraphs);
		Assert.assertEquals(statusGraphs.size(), 2);

		assertUniqueContains(statusGraphs, null, "A", "ATask");
		assertUniqueContains(statusGraphs, null, "B", "BTask");
	}

	/**
	 * null -> (A -> C -> (A,D),B)
	 */
	@Test
	public void test6() {
		IStatusGraphRegistry statusGraphRegistry = StatusGraphRegistryBuilder.newBuilder()
				.addStatusGraphs(BusinessObject.class,
						StatusGraphsBuilder.<String> newBuilder()
								.addNextStatusGraph("A", "ATask",
										StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("C", "CTask",
												StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask").addNextStatusGraph("D", "DTask")))
								.addNextStatusGraph("B", "BTask").build())
				.build();

		List<IStatusGraph<String>> statusGraphs = statusGraphRegistry.getNextStatusGraphsByTaskObjectType(BusinessObject.class, null, "A");
		Assert.assertNotNull(statusGraphs);
		Assert.assertEquals(statusGraphs.size(), 1);

		assertUniqueContains(statusGraphs, "A", "C", "CTask");
	}

	/**
	 * null -> (A -> C -> (A,D),B)
	 */
	@Test
	public void test7() {
		IStatusGraphRegistry statusGraphRegistry = StatusGraphRegistryBuilder.newBuilder()
				.addStatusGraphs(BusinessObject.class,
						StatusGraphsBuilder.<String> newBuilder()
								.addNextStatusGraph("A", "ATask",
										StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("C", "CTask",
												StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask").addNextStatusGraph("D", "DTask")))
								.addNextStatusGraph("B", "BTask").build())
				.build();

		List<IStatusGraph<String>> statusGraphs = statusGraphRegistry.getNextStatusGraphsByTaskObjectType(BusinessObject.class, null, "B");
		Assert.assertNotNull(statusGraphs);
		Assert.assertEquals(statusGraphs.size(), 0);
	}

	private static <E extends Object> void assertUniqueContains(List<IStatusGraph<E>> statusGraphs, E previousStatus, E currentStatus, String updateStatusTaskServiceCode) {
		boolean ok = false;
		if (statusGraphs != null && !statusGraphs.isEmpty()) {
			for (IStatusGraph<E> statusGraph : statusGraphs) {
				if (Objects.equals(statusGraph.getPreviousStatus(), previousStatus) && Objects.equals(statusGraph.getCurrentStatus(), currentStatus)
						&& Objects.equals(statusGraph.getUpdateStatusTaskServiceCode(), updateStatusTaskServiceCode)) {
					if (ok) {
						Assert.assertTrue("Not unique " + previousStatus + " " + currentStatus + " " + updateStatusTaskServiceCode, ok);
					} else {
						ok = true;
					}
				}
			}
		}
		Assert.assertTrue("Not contains " + previousStatus + " " + currentStatus + " " + updateStatusTaskServiceCode, ok);
	}
}
