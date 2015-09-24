package com.synaptix.taskmanager.engine.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.graph.StatusGraphRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.UpdateStatusTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.test.simple.BusinessObject;
import com.synaptix.taskmanager.engine.test.simple.MultiUpdateStatusTaskService;

public class SimpleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.statusGraphRegistry(
						StatusGraphRegistryBuilder.newBuilder().addStatusGraphs(BusinessObject.class, StatusGraphsBuilder.<String> newBuilder().addNextStatusGraph("A", "A").build()).build())
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(TaskObjectManagerBuilder.newBuilder(BusinessObject.class).build()).build())
				.taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder()
						.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("A", new MultiUpdateStatusTaskService("A")).build()).build())
				.build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "A");
	}

}
