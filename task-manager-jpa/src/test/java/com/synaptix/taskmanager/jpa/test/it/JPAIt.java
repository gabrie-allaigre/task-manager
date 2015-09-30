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
import com.synaptix.taskmanager.jpa.model.Task;
import com.synaptix.taskmanager.jpa.test.data.*;
import com.synaptix.taskmanager.model.ITaskCluster;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.Query;
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
	 * Add taskObject tto existing cluster
	 *
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

		showTasks();


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
