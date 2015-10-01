package com.synaptix.taskmanager.jpa.test.it;

import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.TaskDefinitionBuilder;
import com.synaptix.taskmanager.jpa.JPATaskFactory;
import com.synaptix.taskmanager.jpa.JPATaskManagerReaderWriter;
import com.synaptix.taskmanager.jpa.model.Cluster;
import com.synaptix.taskmanager.jpa.model.ClusterDependency;
import com.synaptix.taskmanager.jpa.model.Task;
import com.synaptix.taskmanager.jpa.test.data.*;
import com.synaptix.taskmanager.model.ITaskCluster;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JpaIT {

	/**
	 * null -> A
	 */
	@Test
	public void test1() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.build()).build())
				.taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build())
				.taskFactory(new JPATaskFactory()).taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

		BusinessObject businessObject = new BusinessObject();
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		Assert.assertNull(businessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 2);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * null -> A -> B
	 */
	@Test
	public void test2() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
						.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
						.build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build()).build()).taskFactory(new JPATaskFactory())
				.taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

		BusinessObject businessObject = new BusinessObject();
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 3);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * Test a stop task with DELETE
	 * <p>
	 * null -> A -> (STOP -> B, C)
	 */
	@Test
	public void test3() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
						.addTaskChainCriteria("A", "B", "STOP").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).taskFactory(new JPATaskFactory())
				.taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

		BusinessObject businessObject = new BusinessObject();
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "C");
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 3);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * Test a stop task with CANCEL
	 * <p>
	 * null -> A -> (STOP -> B, C)
	 */
	@Test
	public void test4() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.CANCEL);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
						.addTaskChainCriteria("A", "B", "STOP").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).taskFactory(new JPATaskFactory())
				.taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

		BusinessObject businessObject = new BusinessObject();
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "C");
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 5);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * null -> A -> VERSB -> B
	 */
	@Test
	public void test5() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
						.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
						.addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).taskFactory(new JPATaskFactory())
				.taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getCode(), "VersA");
		Assert.assertEquals(businessObject.getStatus(), "A");

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 4);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		businessObject.setCode("VersB");

		engine.startEngine(taskCluster);

		Assert.assertEquals(businessObject.getCode(), "VersB");
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 4);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * Test two objects separeted
	 * <p>
	 * null -> A -> (VERSB->B,VERSC -> C)
	 */
	@Test
	public void test6() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
						.addTaskChainCriteria("A", "B", "VERSB").addTaskChainCriteria("A", "C", "VERSC").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSC", new VerifyCodeTaskService("VersC")).build()).build()).taskFactory(new JPATaskFactory())
				.taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

		BusinessObject firstBusinessObject = new BusinessObject();
		firstBusinessObject.setCode("VersB");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(firstBusinessObject);

		BusinessObject secondBusinessObject = new BusinessObject();
		secondBusinessObject.setCode("VersC");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(secondBusinessObject);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster firstTaskCluster = engine.startEngine(firstBusinessObject);
		Assert.assertEquals(firstBusinessObject.getStatus(), "B");

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 4);
		Assert.assertEquals(getBusinessObjects().size(), 2);

		ITaskCluster secondTaskCluster = engine.startEngine(secondBusinessObject);
		Assert.assertEquals(secondBusinessObject.getStatus(), "C");

		Assert.assertNotSame(firstTaskCluster, secondTaskCluster);

		Assert.assertTrue(firstTaskCluster.isCheckArchived());
		Assert.assertTrue(secondTaskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 2);
		Assert.assertEquals(getTasks().size(), 8);
		Assert.assertEquals(getBusinessObjects().size(), 2);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * Test
	 * <p>
	 * null -> A -> (VERSB->B,VERSC -> C)
	 */
	@Test
	public void test7() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
						.addTaskChainCriteria("A", "B", "VERSB").addTaskChainCriteria("A", "C", "VERSC").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSC", new VerifyCodeTaskService("VersC")).build()).build()).taskFactory(new JPATaskFactory())
				.taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

		BusinessObject businessObject = new BusinessObject();

		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);
		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "A");

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();
		businessObject.setCode("VersB");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);
		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 6);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		engine.startEngine(businessObject);
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 4);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * Test cycle with A
	 * <p>
	 * null -> A -> (VERSA->CHANGE->A,VERSB->B)
	 */
	@Test
	public void test8() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
						.addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("A", "ATask")).build())
						.addTaskChainCriteria("A", "B", "VERSB").addTaskChainCriteria("A", "A", "VERSA=>CHANGE").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersC")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSA", new VerifyCodeTaskService("VersA")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).taskFactory(new JPATaskFactory())
				.taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

		BusinessObject businessObject = new BusinessObject();
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);
		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "A");

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 7);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();
		businessObject.setCode("VersA");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);
		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		engine.startEngine(taskCluster);

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertEquals(businessObject.getCode(), "VersC");

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 10);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();
		businessObject.setCode("VersB");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);
		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		engine.startEngine(taskCluster);

		Assert.assertEquals(businessObject.getCode(), "VersB");
		Assert.assertEquals(businessObject.getStatus(), "B");

		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 7);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * Remove task object to task cluster
	 * <p>
	 * null -> A -> B
	 * null -> A -> VERSB -> B
	 */
	@Test
	public void test9() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
						.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
						.build()).addTaskObjectManager(TaskObjectManagerBuilder.<String, OtherBusinessObject>newBuilder(OtherBusinessObject.class)
				.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
				.addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).taskFactory(new JPATaskFactory())
				.taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);

		OtherBusinessObject otherBusinessObject = new OtherBusinessObject();
		otherBusinessObject.setCode("VersA");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(otherBusinessObject);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster taskCluster = engine.startEngine(businessObject, otherBusinessObject);

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 7);
		Assert.assertEquals(getBusinessObjects().size(), 2);

		Assert.assertEquals(businessObject.getCode(), "VersA");
		Assert.assertEquals(otherBusinessObject.getCode(), "VersA");
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertEquals(otherBusinessObject.getStatus(), "A");

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();
		otherBusinessObject.setCode("VersB");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(otherBusinessObject);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		engine.removeTaskObjectsFromTaskCluster(otherBusinessObject);

		engine.startEngine(taskCluster);

		Assert.assertEquals(otherBusinessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 3);
		Assert.assertEquals(getBusinessObjects().size(), 2);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * Add taskObject to existing cluster
	 * <p>
	 * null -> A -> (VERSB -> B -> STOP,VERSC -> C -> STOP) -> D
	 */
	@Test
	public void test10() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask",
						StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("D", "DTask"))
								.addNextStatusGraph("C", "CTask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("D", "DTask"))).build()).addTaskChainCriteria("A", "B", "VERSB")
						.addTaskChainCriteria("A", "C", "VERSC").addTaskChainCriteria("B", "D", "STOP").addTaskChainCriteria("C", "D", "STOP").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSC", new VerifyCodeTaskService("VersC")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).taskFactory(new JPATaskFactory())
				.taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

		BusinessObject firstBusinessObject = new BusinessObject();
		firstBusinessObject.setCode("VersB");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(firstBusinessObject);

		BusinessObject secondBusinessObject = new BusinessObject();
		secondBusinessObject.setCode("VersC");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(secondBusinessObject);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		Assert.assertNull(firstBusinessObject.getStatus());

		Assert.assertEquals(getClusters().size(), 0);
		Assert.assertEquals(getTasks().size(), 0);
		Assert.assertEquals(getBusinessObjects().size(), 2);

		ITaskCluster taskCluster = engine.startEngine(firstBusinessObject);

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 6);

		engine.addTaskObjectsToTaskCluster(taskCluster, secondBusinessObject);

		Assert.assertEquals(firstBusinessObject.getStatus(), "B");
		Assert.assertEquals(secondBusinessObject.getStatus(), "C");

		Assert.assertFalse(taskCluster.isCheckArchived());

		ITaskCluster secondTaskCluster = engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(secondBusinessObject);

		Assert.assertSame(taskCluster, secondTaskCluster);

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 12);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * Move task object to new task cluster
	 * <p>
	 * null -> A -> B
	 * null -> A -> VERSB -> B
	 */
	@Test
	public void test11() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
						.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
						.build()).addTaskObjectManager(TaskObjectManagerBuilder.<String, OtherBusinessObject>newBuilder(OtherBusinessObject.class)
				.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
				.addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).taskFactory(new JPATaskFactory())
				.taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);

		OtherBusinessObject otherBusinessObject = new OtherBusinessObject();
		otherBusinessObject.setCode("VersA");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(otherBusinessObject);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster taskCluster = engine.startEngine(businessObject, otherBusinessObject);

		Assert.assertEquals(businessObject.getCode(), "VersA");
		Assert.assertEquals(otherBusinessObject.getCode(), "VersA");
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertEquals(otherBusinessObject.getStatus(), "A");

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 7);
		Assert.assertEquals(getBusinessObjects().size(), 2);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();
		otherBusinessObject.setCode("VersB");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(otherBusinessObject);
		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster newTaskCluster = engine.moveTaskObjectsToNewTaskCluster(otherBusinessObject);

		Assert.assertNotSame(taskCluster, newTaskCluster);

		Assert.assertEquals(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(otherBusinessObject), newTaskCluster);
		Assert.assertEquals(otherBusinessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
		Assert.assertTrue(newTaskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 2);
		Assert.assertEquals(getTasks().size(), 7);
		Assert.assertEquals(getBusinessObjects().size(), 2);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * Move task object to other task cluster
	 */
	@Test
	public void test12() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
						.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
						.addTaskChainCriteria("A", "B", "VERSB").build()).addTaskObjectManager(TaskObjectManagerBuilder.<String, OtherBusinessObject>newBuilder(OtherBusinessObject.class)
				.statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
				.addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).taskFactory(new JPATaskFactory())
				.taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();
		BusinessObject businessObject = new BusinessObject();
		businessObject.setCode("VersA");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);
		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getCode(), "VersA");
		Assert.assertEquals(businessObject.getStatus(), "A");

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 4);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();
		OtherBusinessObject otherBusinessObject = new OtherBusinessObject();
		otherBusinessObject.setCode("VersA");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(otherBusinessObject);
		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster otherTaskCluster = engine.startEngine(otherBusinessObject);

		Assert.assertEquals(otherBusinessObject.getCode(), "VersA");
		Assert.assertEquals(otherBusinessObject.getStatus(), "A");

		Assert.assertEquals(getClusters().size(), 2);
		Assert.assertEquals(getTasks().size(), 8);
		Assert.assertEquals(getBusinessObjects().size(), 2);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();
		businessObject.setCode("VersB");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);

		otherBusinessObject.setCode("VersB");
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(otherBusinessObject);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		engine.moveTaskObjectsToTaskCluster(taskCluster, otherBusinessObject);

		Assert.assertEquals(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(otherBusinessObject), taskCluster);
		Assert.assertEquals(businessObject.getStatus(), "B");
		Assert.assertEquals(otherBusinessObject.getStatus(), "B");
		Assert.assertTrue(taskCluster.isCheckArchived());
		Assert.assertTrue(otherTaskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 2);
		Assert.assertEquals(getTasks().size(), 8);
		Assert.assertEquals(getBusinessObjects().size(), 2);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * Test checkGraphCreated
	 * <p>
	 * null -> A
	 */
	@Test
	public void test13() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.build()).build())
				.taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build())
				.taskFactory(new JPATaskFactory()).taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		Cluster taskCluster = new Cluster();
		taskCluster.setCheckGraphCreated(false);

		jpaTaskManagerReaderWriter.saveNewTaskCluster(taskCluster);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();
		BusinessObject businessObject = new BusinessObject();
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);

		ClusterDependency cd = new ClusterDependency();
		cd.setBusinessTaskObjectId(businessObject.getId());
		cd.setBusinessTaskObjectClass(BusinessObject.class);
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(cd);

		taskCluster.setClusterDependencies(new ArrayList<ClusterDependency>(Collections.singletonList(cd)));
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(taskCluster);

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 0);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		engine.startEngine(taskCluster);

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckGraphCreated());
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertEquals(getClusters().size(), 1);
		Assert.assertEquals(getTasks().size(), 2);
		Assert.assertEquals(getBusinessObjects().size(), 1);

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	/**
	 * Test two parallel task service
	 * <p>
	 * null -> (CHANGE,STOP) -> A
	 */
	@Test
	public void test16() {
		JPAHelper.getInstance().getJpaAccess().start();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "CHANGE,STOP").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersA")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build())
				.taskFactory(new JPATaskFactory()).taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build());

		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();
		BusinessObject businessObject = new BusinessObject();
		JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);
		JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), null);
		Assert.assertTrue(!taskCluster.isCheckArchived());

		JPAHelper.getInstance().getJpaAccess().stop();
	}

	private List<Cluster> getClusters() {
		Query q = JPAHelper.getInstance().getJpaAccess().getEntityManager().createQuery("select t from Cluster t");
		return q.getResultList();
	}

	private static List<BusinessObject> getBusinessObjects() {
		Query q = JPAHelper.getInstance().getJpaAccess().getEntityManager().createQuery("select t from BusinessObject t");
		return q.getResultList();
	}

	private List<Task> getTasks() {
		Query q = JPAHelper.getInstance().getJpaAccess().getEntityManager().createQuery("select t from Task t");
		return q.getResultList();
	}

	private void showTasks() {
		System.out.println("------ Task ------");
		List<Task> tasks = getTasks();
		for (Task t : tasks) {
			System.out.println(t);
		}
		System.out.println("Size: " + tasks.size());
	}
}