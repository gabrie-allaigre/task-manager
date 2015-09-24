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
import com.synaptix.taskmanager.engine.test.data.MultiUpdateStatusTaskService;
import com.synaptix.taskmanager.engine.test.data.StopTaskService;
import com.synaptix.taskmanager.engine.test.data.VerifyCodeTaskService;
import com.synaptix.taskmanager.model.ITaskCluster;

public class MultiIT {

	/**
	 * null -> A -> (VERSB -> B -> STOP,VERSC -> C -> STOP) -> D
	 */
	@Test
	public void test1() {
		TaskManagerEngine engine = new TaskManagerEngine(
				TaskManagerConfigurationBuilder.newBuilder()
						.statusGraphRegistry(
								StatusGraphRegistryBuilder.newBuilder()
										.addStatusGraphs(BusinessObject.class,
												StatusGraphsBuilder.<String> newBuilder()
														.addNextStatusGraph("A", "ATask",
																StatusGraphsBuilder.<String> newBuilder()
																		.addNextStatusGraph("B", "BTask", StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("D", "DTask"))
																		.addNextStatusGraph("C", "CTask",
																				StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("D",
																						"DTask")))
														.build())
										.build())
						.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder()
								.addTaskObjectManager(TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria("A", "B", "VERSB").addTaskChainCriteria("A", "C", "VERSC")
										.addTaskChainCriteria("B", "D", "STOP").addTaskChainCriteria("C", "D", "STOP").build())
								.build())
				.taskDefinitionRegistry(
						TaskDefinitionRegistryBuilder.newBuilder().addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
								.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("VERSC", new VerifyCodeTaskService("VersC")).build())
								.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build())
						.build());

		BusinessObject firstBusinessObject = new BusinessObject();
		firstBusinessObject.setCode("VersB");

		BusinessObject secondBusinessObject = new BusinessObject();
		secondBusinessObject.setCode("VersC");

		Assert.assertNull(firstBusinessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(firstBusinessObject);
		engine.addTaskObjectToTaskCluster(taskCluster, secondBusinessObject);

		Assert.assertEquals(firstBusinessObject.getStatus(), "B");
		Assert.assertEquals(secondBusinessObject.getStatus(), "C");

		Assert.assertFalse(taskCluster.isCheckArchived());
	}

}
