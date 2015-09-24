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
import com.synaptix.taskmanager.engine.test.simple.VerifyCodeTaskService;
import com.synaptix.taskmanager.model.ITaskCluster;

public class MultiIT {

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

		Assert.assertNull(firstBusinessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(firstBusinessObject);
		engine.addTaskObjectToTaskCluster(taskCluster, secondBusinessObject);

		Assert.assertEquals(firstBusinessObject.getStatus(), "B");
		// Assert.assertEquals(secondBusinessObject.getStatus(), "C");
	}

}
