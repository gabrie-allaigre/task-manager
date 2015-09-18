package com.talanlabs.taskmanager.engine.test.it;

import com.talanlabs.taskmanager.engine.TaskManagerEngine;
import com.talanlabs.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.talanlabs.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.talanlabs.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.talanlabs.taskmanager.engine.graph.StatusGraphsBuilder;
import com.talanlabs.taskmanager.engine.listener.LogTaskCycleListener;
import com.talanlabs.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.talanlabs.taskmanager.engine.taskdefinition.TaskDefinitionBuilder;
import com.talanlabs.taskmanager.engine.test.data.BusinessObject;
import com.talanlabs.taskmanager.engine.test.data.MultiUpdateStatusTaskService;
import com.talanlabs.taskmanager.engine.test.data.OtherBusinessObject;
import com.talanlabs.taskmanager.engine.test.data.StopTaskService;
import com.talanlabs.taskmanager.engine.test.data.VerifyCodeTaskService;
import com.talanlabs.taskmanager.model.ITaskCluster;
import org.junit.Assert;
import org.junit.Test;

public class MultiIT {

    /**
     * Add taskObject tto existing cluster
     * <p>
     * null -> A -> (VERSB -> B -> STOP,VERSC -> C -> STOP) -> D
     */
    @Test
    public void test1() {
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
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).build());

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
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
                        .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                        .build()).addTaskObjectManager(TaskObjectManagerBuilder.<String, OtherBusinessObject>newBuilder(OtherBusinessObject.class)
                .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                .addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

        BusinessObject businessObject = new BusinessObject();
        businessObject.setCode("VersA");

        OtherBusinessObject otherBusinessObject = new OtherBusinessObject();
        otherBusinessObject.setCode("VersA");

        ITaskCluster taskCluster = engine.startEngine(businessObject, otherBusinessObject);

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
     * <p>
     * null -> A -> B
     * null -> A -> VERSB -> B
     */
    @Test
    public void test3() {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
                        .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                        .build()).addTaskObjectManager(TaskObjectManagerBuilder.<String, OtherBusinessObject>newBuilder(OtherBusinessObject.class)
                .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                .addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

        engine.addTaskManagerListener(new LogTaskCycleListener());

        BusinessObject businessObject = new BusinessObject();
        businessObject.setCode("VersA");

        OtherBusinessObject otherBusinessObject = new OtherBusinessObject();
        otherBusinessObject.setCode("VersA");

        ITaskCluster taskCluster = engine.startEngine(businessObject, otherBusinessObject);

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
     * <p>
     * null -> A -> VERSB -> B
     * null -> A -> VERSB -> B
     */
    @Test
    public void test4() {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
                        .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                        .build()).addTaskObjectManager(TaskObjectManagerBuilder.<String, OtherBusinessObject>newBuilder(OtherBusinessObject.class)
                .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                .addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

        engine.addTaskManagerListener(new LogTaskCycleListener());

        BusinessObject businessObject = new BusinessObject();
        businessObject.setCode("VersA");

        OtherBusinessObject otherBusinessObject = new OtherBusinessObject();
        otherBusinessObject.setCode("VersA");

        ITaskCluster taskCluster = engine.startEngine(businessObject, otherBusinessObject);

        Assert.assertEquals(businessObject.getCode(), "VersA");
        Assert.assertEquals(otherBusinessObject.getCode(), "VersA");
        Assert.assertEquals(businessObject.getStatus(), "B");
        Assert.assertEquals(otherBusinessObject.getStatus(), "A");

        otherBusinessObject.setCode("VersB");

        ITaskCluster newTaskCluster = engine.moveTaskObjectsToNewTaskCluster(otherBusinessObject);

        Assert.assertNotSame(taskCluster, newTaskCluster);

        Assert.assertEquals(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(otherBusinessObject), newTaskCluster);
        Assert.assertEquals(otherBusinessObject.getStatus(), "B");
        Assert.assertTrue(taskCluster.isCheckArchived());
        Assert.assertTrue(newTaskCluster.isCheckArchived());
    }

    /**
     * Move task object to other task cluster
     * <p>
     * null -> A -> VERSB -> B
     * null -> A -> VERSB -> B
     */
    @Test
    public void test5() {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
                        .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                        .addTaskChainCriteria("A", "B", "VERSB").build()).addTaskObjectManager(TaskObjectManagerBuilder.<String, OtherBusinessObject>newBuilder(OtherBusinessObject.class)
                .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                .addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

        engine.addTaskManagerListener(new LogTaskCycleListener());

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

        engine.moveTaskObjectsToTaskCluster(taskCluster, otherBusinessObject);

        Assert.assertEquals(engine.getTaskManagerConfiguration().getTaskManagerReader().findTaskClusterByTaskObject(otherBusinessObject), taskCluster);
        Assert.assertEquals(businessObject.getStatus(), "B");
        Assert.assertEquals(otherBusinessObject.getStatus(), "B");
        Assert.assertTrue(taskCluster.isCheckArchived());
        Assert.assertTrue(otherTaskCluster.isCheckArchived());
    }
}
