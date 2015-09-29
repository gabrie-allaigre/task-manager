package com.synaptix.taskmanager.engine.test.it;

import org.junit.Assert;
import org.junit.Test;

import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.memory.MemoryTaskManagerReaderWriter;
import com.synaptix.taskmanager.engine.memory.SimpleTaskCluster;
import com.synaptix.taskmanager.engine.taskdefinition.SubTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.StatusTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.test.data.BugTaskService;
import com.synaptix.taskmanager.engine.test.data.BusinessObject;
import com.synaptix.taskmanager.engine.test.data.ChangeCodeTaskService;
import com.synaptix.taskmanager.engine.test.data.MultiUpdateStatusTaskService;
import com.synaptix.taskmanager.engine.test.data.NullTaskService;
import com.synaptix.taskmanager.engine.test.data.SetNowDateTaskService;
import com.synaptix.taskmanager.engine.test.data.StopTaskService;
import com.synaptix.taskmanager.engine.test.data.VerifyCodeTaskService;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public class SimpleIT {

	/**
	 * null -> A
	 */
	@Test
	public void test1() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
				.addTaskObjectManager(TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build()).build())
				.build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build())
				.build());

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
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class)
						.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
						.build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build()).build()).build());

		BusinessObject businessObject = new BusinessObject();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * Test a stop task
	 * <p>
	 * null -> A -> (STOP -> B, C)
	 */
	@Test
	public void test3() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
						.addTaskChainCriteria("A", "B", "STOP").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).build());

		BusinessObject businessObject = new BusinessObject();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "C");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * Test a stop task
	 * <p>
	 * null -> A -> (B,STOP -> C)
	 */
	@Test
	public void test4() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
						.addTaskChainCriteria("A", "C", "STOP").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).build());

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
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class)
						.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
						.addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

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
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class)
						.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
						.addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

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
	 * <p>
	 * null -> A -> (VERSB->B,VERSC -> C)
	 */
	@Test
	public void test6() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
						.addTaskChainCriteria("A", "B", "VERSB").addTaskChainCriteria("A", "C", "VERSC").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSC", new VerifyCodeTaskService("VersC")).build()).build()).build());

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
	 * <p>
	 * null -> A -> (VERSA->CHANGE->A,VERSB->B)
	 */
	@Test
	public void test7() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("A", "ATask")).build())
						.addTaskChainCriteria("A", "B", "VERSB").addTaskChainCriteria("A", "A", "VERSA=>CHANGE").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersB")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSA", new VerifyCodeTaskService("VersA")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

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
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class)
						.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
						.addTaskChainCriteria("A", "B", "(CHANGE,DATE)=>VERSB").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersB")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("DATE", new SetNowDateTaskService()).build()).build()).build());

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");

		Assert.assertNull(businessObject.getDate());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertNotNull(businessObject.getDate());
		Assert.assertEquals(businessObject.getCode(), "VersB");
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	@Test
	public void test9() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
				.addTaskObjectManager(TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build()).build())
				.build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build())
				.build());

		ITaskCluster taskCluster = engine.startEngine((ITaskObject) null);

		Assert.assertNull(taskCluster);
	}

	@Test
	public void test10() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
				.addTaskObjectManager(TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build()).build())
				.build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build())
				.build());

		engine.startEngine((ITaskCluster) null);

		Assert.assertTrue(true);
	}

	@Test
	public void test11() {
		MemoryTaskManagerReaderWriter memoryTaskManagerReaderWriter = new MemoryTaskManagerReaderWriter();

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
				.addTaskObjectManager(TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build()).build())
				.build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build())
				.taskManagerReader(memoryTaskManagerReaderWriter).taskManagerWriter(memoryTaskManagerReaderWriter).build());

		SimpleTaskCluster taskCluster = new SimpleTaskCluster();
		taskCluster.setCheckGraphCreated(false);

		memoryTaskManagerReaderWriter.saveNewTaskCluster(taskCluster);

		BusinessObject businessObject = new BusinessObject();

		memoryTaskManagerReaderWriter.addTaskObjectsInTaskCluster(taskCluster, businessObject);

		engine.startEngine(taskCluster);

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckGraphCreated());
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	/**
	 * null -> A
	 */
	@Test
	public void test12() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
						TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
								.addTaskChainCriteria(null, "A", "BUG").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("BUG", new BugTaskService()).build()).build()).build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), null);
		Assert.assertFalse(taskCluster.isCheckArchived());
	}

	/**
	 * null -> A
	 */
	@Test
	public void test13() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
						TaskObjectManagerBuilder.<String,BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
								.addTaskChainCriteria(null, "A", "NULL").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("NULL", new NullTaskService()).build()).build()).build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), null);
		Assert.assertFalse(taskCluster.isCheckArchived());
	}
}
