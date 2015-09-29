package com.synaptix.taskmanager.engine.test.it;

import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.SubTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.GeneralTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.test.data.*;
import com.synaptix.taskmanager.model.ITaskCluster;
import org.junit.Assert;
import org.junit.Test;

public class MultiIT {

	/**
	 * Add taskObject tto existing cluster
	 *
	 * null -> A -> (VERSB -> B -> STOP,VERSC -> C -> STOP) -> D
	 */
	@Test
	public void test1() {
		TaskManagerEngine engine = new TaskManagerEngine(
				TaskManagerConfigurationBuilder.newBuilder()
						.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
								.addTaskObjectManager(TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String> newBuilder()
										.addNextStatusGraph("A", "ATask",
												StatusGraphsBuilder.<String> newBuilder()
														.addNextStatusGraph("B", "BTask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("D", "DTask"))
														.addNextStatusGraph("C", "CTask",
																StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("D",
																		"DTask")))
										.build()).addTaskChainCriteria("A", "B", "VERSB").addTaskChainCriteria("A", "C", "VERSC")
										.addTaskChainCriteria("B", "D", "STOP").addTaskChainCriteria("C", "D", "STOP").build())
								.build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addGeneralTaskDefinition(GeneralTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addGeneralTaskDefinition(GeneralTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addGeneralTaskDefinition(GeneralTaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
								.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build())
								.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSC", new VerifyCodeTaskService("VersC")).build())
								.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build())
						.build());

		BusinessObject firstBusinessObject = new BusinessObject();
		firstBusinessObject.setCode("VersB");

		BusinessObject secondBusinessObject = new BusinessObject();
		secondBusinessObject.setCode("VersC");

		Assert.assertNull(firstBusinessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(firstBusinessObject);
		engine.addTaskObjectsToTaskCluster(taskCluster, secondBusinessObject);

		Assert.assertEquals(firstBusinessObject.getStatus(), "B");
		Assert.assertEquals(secondBusinessObject.getStatus(), "C");

		Assert.assertFalse(taskCluster.isCheckArchived());

		ITaskCluster secondTaskCluster = engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(secondBusinessObject);

		Assert.assertSame(taskCluster, secondTaskCluster);
	}

	/**
	 * Start engine with 2 task objects
	 */
	@Test
	public void test2() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
						.addTaskObjectManager(TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build()).build())
						.addTaskObjectManager(TaskObjectManagerBuilder.<String,OtherBusinessObject>newBuilder(OtherBusinessObject.class).statusGraphs(StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("B", "BTask")).build()).addTaskChainCriteria(
								"A", "B", "VERSB").build())
						.build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addGeneralTaskDefinition(GeneralTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addGeneralTaskDefinition(GeneralTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build())
				.build());

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");

		OtherBusinessObject otherBusinessObject = new OtherBusinessObject();
		otherBusinessObject.setCode("VersA");

		ITaskCluster taskCluster = engine.startEngine(businessObject,otherBusinessObject);

		Assert.assertEquals(businessObject.getCode(), "VersA");
		Assert.assertEquals(otherBusinessObject.getCode(), "VersA");
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertEquals(otherBusinessObject.getStatus(), "A");

		otherBusinessObject.setCode("VersB");

		engine.startEngine(taskCluster);

		Assert.assertEquals(otherBusinessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * Remove task object to task cluster
	 */
	@Test
	public void test3() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
						.addTaskObjectManager(TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build()).build())
						.addTaskObjectManager(TaskObjectManagerBuilder.<String,OtherBusinessObject>newBuilder(OtherBusinessObject.class).statusGraphs(StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("B", "BTask")).build()).addTaskChainCriteria(
								"A", "B", "VERSB").build())
						.build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addGeneralTaskDefinition(GeneralTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addGeneralTaskDefinition(GeneralTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build())
				.build());

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");

		OtherBusinessObject otherBusinessObject = new OtherBusinessObject();
		otherBusinessObject.setCode("VersA");

		ITaskCluster taskCluster = engine.startEngine(businessObject,otherBusinessObject);

		Assert.assertEquals(businessObject.getCode(), "VersA");
		Assert.assertEquals(otherBusinessObject.getCode(), "VersA");
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertEquals(otherBusinessObject.getStatus(), "A");

		otherBusinessObject.setCode("VersB");

		engine.removeTaskObjectsFromTaskCluster(otherBusinessObject);

		engine.startEngine(taskCluster);

		Assert.assertEquals(otherBusinessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * Move task object to new task cluster
	 */
	@Test
	public void test4() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
						.addTaskObjectManager(TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build()).build())
						.addTaskObjectManager(TaskObjectManagerBuilder.<String,OtherBusinessObject>newBuilder(OtherBusinessObject.class).statusGraphs(StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("B", "BTask")).build()).addTaskChainCriteria(
								"A", "B", "VERSB").build())
						.build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addGeneralTaskDefinition(GeneralTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addGeneralTaskDefinition(GeneralTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build())
				.build());

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");

		OtherBusinessObject otherBusinessObject = new OtherBusinessObject();
		otherBusinessObject.setCode("VersA");

		ITaskCluster taskCluster = engine.startEngine(businessObject,otherBusinessObject);

		Assert.assertEquals(businessObject.getCode(), "VersA");
		Assert.assertEquals(otherBusinessObject.getCode(), "VersA");
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertEquals(otherBusinessObject.getStatus(), "A");

		otherBusinessObject.setCode("VersB");

		ITaskCluster newTaskCluster = engine.moveTaskObjectsToNewTaskCluster(otherBusinessObject);

		Assert.assertNotSame(taskCluster,newTaskCluster);

		Assert.assertEquals(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(otherBusinessObject), newTaskCluster);
		Assert.assertEquals(otherBusinessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
		Assert.assertTrue(newTaskCluster.isCheckArchived());
	}

	/**
	 * Move task object to new task cluster
	 */
	@Test
	public void test5() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
						.addTaskObjectManager(TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build()).addTaskChainCriteria(
								"A", "B", "VERSB").build())
						.addTaskObjectManager(TaskObjectManagerBuilder.<String,OtherBusinessObject>newBuilder(OtherBusinessObject.class).statusGraphs(StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("B", "BTask")).build()).addTaskChainCriteria(
								"A", "B", "VERSB").build())
						.build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addGeneralTaskDefinition(GeneralTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addGeneralTaskDefinition(GeneralTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build())
				.build());

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getCode(), "VersA");
		Assert.assertEquals(businessObject.getStatus(), "A");

		OtherBusinessObject otherBusinessObject = new OtherBusinessObject();
		otherBusinessObject.setCode("VersA");

		ITaskCluster otherTaskCluster = engine.startEngine(otherBusinessObject);

		Assert.assertEquals(otherBusinessObject.getCode(), "VersA");
		Assert.assertEquals(otherBusinessObject.getStatus(), "A");

		businessObject.setCode("VersB");
		otherBusinessObject.setCode("VersB");

		engine.moveTaskObjectsToTaskCluster(taskCluster,otherBusinessObject);

		Assert.assertEquals(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(otherBusinessObject), taskCluster);
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertEquals(otherBusinessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
		Assert.assertTrue(otherTaskCluster.isCheckArchived());
	}
}
