package com.synaptix.taskmanager.engine.test.unit;

import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.test.data.BusinessObject;
import com.synaptix.taskmanager.engine.test.data.OtherBusinessObject;

import java.util.List;
import java.util.Objects;

public class TaskObjectTest {

	@Test
	public void test1() {
		ITaskObjectManager<String, BusinessObject> taskObjectManager = TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).build();

		Assert.assertEquals(taskObjectManager.getTaskObjectClass(), BusinessObject.class);
		Assert.assertNull(taskObjectManager.getTaskChainCriteria(null, null, null));
	}

	@Test
	public void test2() {
		ITaskObjectManager<String, BusinessObject> taskObjectManager = TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB").build();

		Assert.assertEquals(taskObjectManager.getTaskChainCriteria(null, null, "A"), "VERSA->VERSB");
	}

	@Test
	public void test3() {
		ITaskObjectManager<String, BusinessObject> taskObjectManager = TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB").build();

		Assert.assertEquals(taskObjectManager.getTaskChainCriteria(null, null, "A"), "VERSA->VERSB");
	}

	@Test
	public void test4() {
		ITaskObjectManager<String, BusinessObject> taskObjectManager = TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB")
				.addTaskChainCriteria("B", "C", "VERSC").build();

		Assert.assertEquals(taskObjectManager.getTaskChainCriteria(null, "B", "C"), "VERSC");
	}

	@Test
	public void test5() {
		ITaskObjectManager<String, BusinessObject> taskObjectManager = TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB")
				.addTaskChainCriteria("B", "C", "VERSC").build();

		ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(taskObjectManager).build();

		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(BusinessObject.class), taskObjectManager);
		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(new BusinessObject()), taskObjectManager);
	}

	@Test
	public void test6() {
		ITaskObjectManager<String, BusinessObject> taskObjectManager = TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB")
				.addTaskChainCriteria("B", "C", "VERSC").build();
		ITaskObjectManager<String, OtherBusinessObject> otherTaskObjectManager = TaskObjectManagerBuilder.<String,OtherBusinessObject>newBuilder(OtherBusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB").build();

		ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(taskObjectManager).addTaskObjectManager(otherTaskObjectManager)
				.build();

		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(BusinessObject.class), taskObjectManager);
		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(new BusinessObject()), taskObjectManager);

		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(OtherBusinessObject.class), otherTaskObjectManager);
		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(new OtherBusinessObject()), otherTaskObjectManager);
	}

	/**
	 * null -> (A -> C -> (A,D),B)
	 */
	@Test
	public void test7() {
		ITaskObjectManager<String, BusinessObject> taskObjectManager = TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
				.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("C", "CTask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").addNextStatusGraph("D", "DTask"))).addNextStatusGraph("B", "BTask")
				.build()).build();

		List<IStatusGraph<String>> statusGraphs = taskObjectManager.getNextStatusGraphsByTaskObjectType(null, null);
		Assert.assertNotNull(statusGraphs);
		Assert.assertEquals(statusGraphs.size(), 2);

		StatusGraphTest.assertUniqueContains(statusGraphs, null, "A", "ATask");
		StatusGraphTest.assertUniqueContains(statusGraphs, null, "B", "BTask");
	}

	/**
	 * null -> (A -> C -> (A,D),B)
	 */
	@Test
	public void test8() {
		ITaskObjectManager<String, BusinessObject> taskObjectManager = TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
				.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("C", "CTask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").addNextStatusGraph("D", "DTask"))).addNextStatusGraph("B", "BTask")
				.build()).build();

		List<IStatusGraph<String>> statusGraphs = taskObjectManager.getNextStatusGraphsByTaskObjectType(null, "A");
		Assert.assertNotNull(statusGraphs);
		Assert.assertEquals(statusGraphs.size(), 1);

		StatusGraphTest.assertUniqueContains(statusGraphs, "A", "C", "CTask");
	}

	/**
	 * null -> (A -> C -> (A,D),B)
	 */
	@Test
	public void test9() {
		ITaskObjectManager<String, BusinessObject> taskObjectManager = TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
				.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("C", "CTask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").addNextStatusGraph("D", "DTask"))).addNextStatusGraph("B", "BTask")
				.build()).build();

		List<IStatusGraph<String>> statusGraphs = taskObjectManager.getNextStatusGraphsByTaskObjectType(null, "B");
		Assert.assertNotNull(statusGraphs);
		Assert.assertEquals(statusGraphs.size(), 0);
	}

	/**
	 * null -> (A -> C -> (A,D),B)
	 */
	@Test
	public void test10() {
		ITaskObjectManager<String, BusinessObject> taskObjectManager = TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
				.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("C", "CTask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").addNextStatusGraph("D", "DTask"))).addNextStatusGraph("B", "BTask")
				.build()).build();

		List<IStatusGraph<String>> statusGraphs = taskObjectManager.getNextStatusGraphsByTaskObjectType(null, "C");
		Assert.assertNotNull(statusGraphs);
		Assert.assertEquals(statusGraphs.size(), 2);

		StatusGraphTest.assertUniqueContains(statusGraphs, "C", "A", "ATask");
		StatusGraphTest.assertUniqueContains(statusGraphs, "C", "D", "DTask");
	}
}
