package com.synaptix.taskmanager.engine.test.it;

import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.TaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.test.data.*;
import com.synaptix.taskmanager.model.ITaskCluster;
import org.junit.Assert;
import org.junit.Test;

public class ContextIT {

	@Test
	public void test1() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "OPTION").build()).addTaskObjectManager(
				TaskObjectManagerBuilder.<String, OptionObject>newBuilder(OptionObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("CLO", "CLOTask").build())
						.build()).build())
				.taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLOTask", new MultiUpdateStatusTaskService("CLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("OPTION", new AddOptionTaskService()).build())
						.build())
				.build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertNotNull(businessObject.getOptionObject());

		Assert.assertEquals(businessObject.getOptionObject().getStatus(), "CLO");

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	@Test
	public void test2() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "OPTION").build()).addTaskObjectManager(
				TaskObjectManagerBuilder.<String, OptionObject>newBuilder(OptionObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("CLO", "CLOTask").build())
						.addTaskChainCriteria(null, "CLO", "CHANGE").build()).build())
				.taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLOTask", new MultiUpdateStatusTaskService("CLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("OPTION", new AddOptionTaskService()).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersCLO")).build())
						.build())
				.build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertNotNull(businessObject.getOptionObject());
		Assert.assertEquals(businessObject.getOptionObject().getCode(), "VersCLO");
		Assert.assertEquals(businessObject.getOptionObject().getStatus(), "CLO");

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckArchived());
	}

	@Test
	public void test3() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "OPTION").build()).addTaskObjectManager(
				TaskObjectManagerBuilder.<String, OptionObject>newBuilder(OptionObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("CLO", "CLOTask").build())
						.addTaskChainCriteria(null, "CLO", "VERSCLO").build()).build())
				.taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLOTask", new MultiUpdateStatusTaskService("CLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("OPTION", new AddOptionTaskService()).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSCLO", new VerifyCodeTaskService("VersCLO")).build())
						.build())
				.build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertFalse(taskCluster.isCheckArchived());

		Assert.assertNotNull(businessObject.getOptionObject());
		Assert.assertEquals(businessObject.getOptionObject().getStatus(), null);
		businessObject.getOptionObject().setCode("VersCLO");

		engine.startEngine(businessObject);

		Assert.assertEquals(businessObject.getOptionObject().getStatus(), "CLO");

		Assert.assertTrue(taskCluster.isCheckArchived());
	}
}
