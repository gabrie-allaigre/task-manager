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

	/**
	 * StartEngine
	 */
	@Test
	public void test1() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "OPTION=>STOP").build()).addTaskObjectManager(
				TaskObjectManagerBuilder.<String, OptionObject>newBuilder(OptionObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("CLO", "CLOTask").build())
						.build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLOTask", new MultiUpdateStatusTaskService("CLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("OPTION", new StartOptionTaskService()).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertNotNull(businessObject.getOptionObject());

		Assert.assertEquals(businessObject.getOptionObject().getStatus(), "CLO");

		Assert.assertEquals(businessObject.getStatus(), null);
		Assert.assertFalse(taskCluster.isCheckArchived());
		Assert.assertTrue(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject.getOptionObject()).isCheckArchived());

		Assert.assertNotSame(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject),
				engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject.getOptionObject()));
	}

	/**
	 * StartEngine
	 */
	@Test
	public void test2() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "OPTION=>STOP").build()).addTaskObjectManager(
				TaskObjectManagerBuilder.<String, OptionObject>newBuilder(OptionObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("CLO", "CLOTask").build())
						.addTaskChainCriteria(null, "CLO", "VERIFY").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLOTask", new MultiUpdateStatusTaskService("CLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("OPTION", new StartOptionTaskService()).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERIFY", new VerifyCodeTaskService("VERSCLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).build());

		OptionObject optionObject = new OptionObject();
		Assert.assertNull(optionObject.getStatus());

		ITaskCluster taskCluster1 = engine.startEngine(optionObject);

		Assert.assertEquals(optionObject.getStatus(), null);
		Assert.assertFalse(taskCluster1.isCheckArchived());

		optionObject.setCode("VERSCLO");

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		businessObject.setOptionObject(optionObject);

		ITaskCluster taskCluster2 = engine.startEngine(businessObject);

		Assert.assertNotNull(businessObject.getOptionObject());
		Assert.assertEquals(businessObject.getOptionObject(), optionObject);

		Assert.assertEquals(businessObject.getStatus(), null);
		Assert.assertEquals(optionObject.getStatus(), "CLO");
		Assert.assertFalse(taskCluster2.isCheckArchived());
		Assert.assertTrue(taskCluster1.isCheckArchived());

		Assert.assertNotSame(taskCluster1, taskCluster2);
	}

	/**
	 * Add
	 */
	@Test
	public void test3() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "OPTION").build()).addTaskObjectManager(
				TaskObjectManagerBuilder.<String, OptionObject>newBuilder(OptionObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("CLO", "CLOTask").build())
						.build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLOTask", new MultiUpdateStatusTaskService("CLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("OPTION", new AddOptionTaskService()).build()).build()).build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertNotNull(businessObject.getOptionObject());

		Assert.assertEquals(businessObject.getOptionObject().getStatus(), "CLO");

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertEquals(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject),
				engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject.getOptionObject()));
	}

	/**
	 * Add
	 */
	@Test
	public void test4() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "OPTION").build()).addTaskObjectManager(
				TaskObjectManagerBuilder.<String, OptionObject>newBuilder(OptionObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("CLO", "CLOTask").build())
						.addTaskChainCriteria(null, "CLO", "CHANGE").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLOTask", new MultiUpdateStatusTaskService("CLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("OPTION", new AddOptionTaskService()).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersCLO")).build()).build()).build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		ITaskCluster taskCluster = engine.startEngine(businessObject);

		Assert.assertNotNull(businessObject.getOptionObject());
		Assert.assertEquals(businessObject.getOptionObject().getCode(), "VersCLO");
		Assert.assertEquals(businessObject.getOptionObject().getStatus(), "CLO");

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertEquals(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject),
				engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject.getOptionObject()));
	}

	/**
	 * Add
	 */
	@Test
	public void test5() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "OPTION").build()).addTaskObjectManager(
				TaskObjectManagerBuilder.<String, OptionObject>newBuilder(OptionObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("CLO", "CLOTask").build())
						.addTaskChainCriteria(null, "CLO", "VERSCLO").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLOTask", new MultiUpdateStatusTaskService("CLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("OPTION", new AddOptionTaskService()).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSCLO", new VerifyCodeTaskService("VersCLO")).build()).build()).build());

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

		Assert.assertEquals(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject),
				engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject.getOptionObject()));
	}

	/**
	 * Remove
	 */
	@Test
	public void test6() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "OPTION").build()).addTaskObjectManager(
				TaskObjectManagerBuilder.<String, OptionObject>newBuilder(OptionObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("CLO", "CLOTask").build())
						.addTaskChainCriteria(null, "CLO", "STOP").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLOTask", new MultiUpdateStatusTaskService("CLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("OPTION", new RemoveOptionTaskService()).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		OptionObject optionObject = new OptionObject();
		Assert.assertNull(optionObject.getStatus());

		businessObject.setOptionObject(optionObject);

		ITaskCluster taskCluster = engine.startEngine(businessObject, optionObject);

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertNotNull(optionObject);
		Assert.assertEquals(optionObject.getStatus(), null);

		Assert.assertNull(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject.getOptionObject()));
	}

	/**
	 * Move to new cluster
	 */
	@Test
	public void test7() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "OPTION").build()).addTaskObjectManager(
				TaskObjectManagerBuilder.<String, OptionObject>newBuilder(OptionObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("CLO", "CLOTask").build())
						.addTaskChainCriteria(null, "CLO", "STOP").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLOTask", new MultiUpdateStatusTaskService("CLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("OPTION", new MoveToNewOptionTaskService()).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).build());

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		OptionObject optionObject = new OptionObject();
		Assert.assertNull(optionObject.getStatus());

		businessObject.setOptionObject(optionObject);

		ITaskCluster taskCluster = engine.startEngine(businessObject, optionObject);

		Assert.assertEquals(businessObject.getStatus(), "A");
		Assert.assertTrue(taskCluster.isCheckArchived());

		Assert.assertNotNull(optionObject);
		Assert.assertEquals(optionObject.getStatus(), null);

		Assert.assertFalse(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(optionObject).isCheckArchived());

		Assert.assertNotSame(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject),
				engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(optionObject));
	}

	/**
	 * Move to cluster
	 */
	@Test
	public void test8() {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
				TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
						.addTaskChainCriteria(null, "A", "OPTION=>STOP").build()).addTaskObjectManager(
				TaskObjectManagerBuilder.<String, OptionObject>newBuilder(OptionObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("CLO", "CLOTask").build())
						.addTaskChainCriteria(null, "CLO", "VERIFY").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLOTask", new MultiUpdateStatusTaskService("CLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("OPTION", new MoveOptionTaskService()).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERIFY", new VerifyCodeTaskService("VERSCLO")).build())
						.addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).build());

		OptionObject optionObject = new OptionObject();
		Assert.assertNull(optionObject.getStatus());

		ITaskCluster taskCluster1 = engine.startEngine(optionObject);

		Assert.assertEquals(optionObject.getStatus(), null);
		Assert.assertFalse(taskCluster1.isCheckArchived());

		optionObject.setCode("VERSCLO");

		BusinessObject businessObject = new BusinessObject();
		Assert.assertNull(businessObject.getStatus());

		businessObject.setOptionObject(optionObject);

		ITaskCluster taskCluster2 = engine.startEngine(businessObject);

		Assert.assertNotNull(businessObject.getOptionObject());
		Assert.assertEquals(businessObject.getOptionObject(), optionObject);

		Assert.assertEquals(businessObject.getStatus(), null);
		Assert.assertEquals(optionObject.getStatus(), "CLO");
		Assert.assertFalse(taskCluster2.isCheckArchived());
		Assert.assertTrue(taskCluster1.isCheckArchived());

		Assert.assertEquals(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(businessObject),
				engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(optionObject));
		Assert.assertNotSame(taskCluster1, engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(optionObject));

	}
}
