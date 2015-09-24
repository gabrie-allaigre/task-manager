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
import com.synaptix.taskmanager.engine.test.simple.BusinessObject;
import com.synaptix.taskmanager.engine.test.simple.MultiUpdateStatusTaskService;
import com.synaptix.taskmanager.engine.test.simple.StopTaskService;

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

		engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "A");
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
		Assert.assertNull(businessObject.getStatus());

		engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "B");
	}

	/**
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
		Assert.assertNull(businessObject.getStatus());

		engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "C");
	}

	/**
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
		Assert.assertNull(businessObject.getStatus());

		engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "B");
	}
}
