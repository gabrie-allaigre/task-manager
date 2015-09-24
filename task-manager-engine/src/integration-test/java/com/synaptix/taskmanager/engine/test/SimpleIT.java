package com.synaptix.taskmanager.engine.test;

import org.junit.Assert;
import org.junit.Test;

import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.graph.StatusGraphRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.NormalTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.UpdateStatusTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.test.data.BusinessObject;
import com.synaptix.taskmanager.engine.test.data.ChangeCodeTaskService;
import com.synaptix.taskmanager.engine.test.data.MultiUpdateStatusTaskService;
import com.synaptix.taskmanager.engine.test.data.SetNowDateTaskService;
import com.synaptix.taskmanager.engine.test.data.StopTaskService;
import com.synaptix.taskmanager.engine.test.data.VerifyCodeTaskService;
import com.synaptix.taskmanager.model.ITaskCluster;

public class SimpleIT {

	/**
	 * null -> A
	 */
	@Test
	public void test1() {
		TaskManagerEngine engine = new TaskManagerEngine(
				TaskManagerConfigurationBuilder.newBuilder()
						.statusGraphRegistry(StatusGraphRegistryBuilder.newBuilder()
								.addStatusGraphs(BusinessObject.class, StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask").build()).build())
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(TaskObjectManagerBuilder.newBuilder(BusinessObject.class).build()).build())
				.taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder()
						.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build()).build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * null -> A -> B
	 */
	@Test
	public void test2() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.statusGraphRegistry(StatusGraphRegistryBuilder.newBuilder()
						.addStatusGraphs(BusinessObject.class,
								StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("B", "BTask")).build())
						.build())
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(TaskObjectManagerBuilder.newBuilder(BusinessObject.class).build()).build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build()).build())
				.build());

		BusinessObject businessObject = new BusinessObject();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * Test a stop task
	 * 
	 * null -> A -> (STOP -> B, C)
	 */
	@Test
	public void test3() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.statusGraphRegistry(StatusGraphRegistryBuilder.newBuilder()
						.addStatusGraphs(BusinessObject.class,
								StatusGraphsBuilder.<String> newBuilder()
										.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
						.build())
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
						.addTaskObjectManager(TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria("A", "B", "STOP").build()).build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build())
				.build());

		BusinessObject businessObject = new BusinessObject();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "C");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * Test a stop task
	 * 
	 * null -> A -> (B,STOP -> C)
	 */
	@Test
	public void test4() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.statusGraphRegistry(StatusGraphRegistryBuilder.newBuilder()
						.addStatusGraphs(BusinessObject.class,
								StatusGraphsBuilder.<String> newBuilder()
										.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
						.build())
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
						.addTaskObjectManager(TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria("A", "C", "STOP").build()).build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build())
				.build());

		BusinessObject businessObject = new BusinessObject();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * null -> A -> VERSB -> B
	 */
	@Test
	public void test5() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.statusGraphRegistry(StatusGraphRegistryBuilder.newBuilder()
						.addStatusGraphs(BusinessObject.class,
								StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("B", "BTask")).build())
						.build())
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
						.addTaskObjectManager(TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria("A", "B", "VERSB").build()).build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build())
				.build());

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getCode(), "VersA");
		Assert.assertEquals(businessObject.getStatus(), "A");

		businessObject.setCode("VersB");

		engine.startEngine(taskCluster);

		Assert.assertEquals(businessObject.getCode(), "VersB");
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * null -> A -> VERSB -> B
	 */
	@Test
	public void test5bis() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.statusGraphRegistry(StatusGraphRegistryBuilder.newBuilder()
						.addStatusGraphs(BusinessObject.class,
								StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("B", "BTask")).build())
						.build())
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
						.addTaskObjectManager(TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria("A", "B", "VERSB").build()).build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build())
				.build());

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getCode(), "VersA");
		Assert.assertEquals(businessObject.getStatus(), "A");

		businessObject.setCode("VersB");

		engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getCode(), "VersB");
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * Test two objects separeted
	 * 
	 * null -> A -> (VERSB->B,VERSC -> C)
	 */
	@Test
	public void test6() {
		TaskManagerEngine engine = new TaskManagerEngine(
				TaskManagerConfigurationBuilder.newBuilder()
						.statusGraphRegistry(
								StatusGraphRegistryBuilder.newBuilder()
										.addStatusGraphs(BusinessObject.class,
												StatusGraphsBuilder.<String> newBuilder()
														.addNextStatusGraph("A", "ATask",
																StatusGraphsBuilder.<String> newBuilder()
																		.addNextStatusGraph("B",
																				"BTask")
																		.addNextStatusGraph("C", "CTask"))
														.build())
										.build())
						.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
								.addTaskObjectManager(TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria("A", "B", "VERSB").addTaskChainCriteria("A", "C", "VERSC").build())
								.build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("VERSC", new VerifyCodeTaskService("VersC")).build()).build())
						.build());

		BusinessObject firstBusinessObject = new BusinessObject();
		firstBusinessObject.setCode("VersB");

		BusinessObject secondBusinessObject = new BusinessObject();
		secondBusinessObject.setCode("VersC");

		ITaskCluster firstTaskCluster = engine.startEngine(firstBusinessObject);
		Assert.assertEquals(firstBusinessObject.getStatus(), "B");

		ITaskCluster secondTaskCluster = engine.startEngine(secondBusinessObject);
		Assert.assertEquals(secondBusinessObject.getStatus(), "C");

		Assert.assertNotSame(firstTaskCluster, secondTaskCluster);

		Assert.assertTrue(firstTaskCluster.isCheckArchived());
		Assert.assertTrue(secondTaskCluster.isCheckArchived());
	}

	/**
	 * Test cycle with A
	 * 
	 * null -> A -> (VERSA->CHANGE->A,VERSB->B)
	 */
	@Test
	public void test7() {
		TaskManagerEngine engine = new TaskManagerEngine(
				TaskManagerConfigurationBuilder.newBuilder()
						.statusGraphRegistry(
								StatusGraphRegistryBuilder.newBuilder()
										.addStatusGraphs(BusinessObject.class,
												StatusGraphsBuilder.<String> newBuilder()
														.addNextStatusGraph("A", "ATask",
																StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("A",
																		"ATask"))
														.build())
										.build())
						.taskObjectManagerRegistry(
								TaskObjectManagerRegistryBuilder.newBuilder()
										.addTaskObjectManager(TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria("A", "B", "VERSB")
												.addTaskChainCriteria("A", "A", "VERSA=>CHANGE").build())
										.build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersB")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("VERSA", new VerifyCodeTaskService("VersA")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build())
						.build());

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getCode(), "VersB");
		Assert.assertEquals(businessObject.getStatus(), "B");

		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * null -> A -> (CHANGE,DATE) => VERSB -> B
	 */
	@Test
	public void test8() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.statusGraphRegistry(StatusGraphRegistryBuilder.newBuilder()
						.addStatusGraphs(BusinessObject.class,
								StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("B", "BTask")).build())
						.build())
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
						.addTaskObjectManager(TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria("A", "B", "(CHANGE,DATE)->VERSB").build()).build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersB")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("DATE", new SetNowDateTaskService()).build()).build())
				.build());

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");

		Assert.assertNull(businessObject.getDate());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertNotNull(businessObject.getDate());
		Assert.assertEquals(businessObject.getCode(), "VersB");
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}
}
