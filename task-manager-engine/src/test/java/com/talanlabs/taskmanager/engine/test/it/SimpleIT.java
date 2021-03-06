package com.talanlabs.taskmanager.engine.test.it;

import com.talanlabs.taskmanager.engine.TaskManagerEngine;
import com.talanlabs.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.talanlabs.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.talanlabs.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.talanlabs.taskmanager.engine.graph.StatusGraphsBuilder;
import com.talanlabs.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.talanlabs.taskmanager.engine.memory.MemoryTaskManagerReaderWriter;
import com.talanlabs.taskmanager.engine.memory.SimpleTaskCluster;
import com.talanlabs.taskmanager.engine.taskdefinition.TaskDefinitionBuilder;
import com.talanlabs.taskmanager.engine.test.data.BugTaskService;
import com.talanlabs.taskmanager.engine.test.data.BusinessObject;
import com.talanlabs.taskmanager.engine.test.data.ChangeCodeTaskService;
import com.talanlabs.taskmanager.engine.test.data.MultiUpdateStatusTaskService;
import com.talanlabs.taskmanager.engine.test.data.NullTaskService;
import com.talanlabs.taskmanager.engine.test.data.SetNowDateTaskService;
import com.talanlabs.taskmanager.engine.test.data.StopTaskService;
import com.talanlabs.taskmanager.engine.test.data.VerifyCodeTaskService;
import com.talanlabs.taskmanager.model.ITaskCluster;
import com.talanlabs.taskmanager.model.ITaskObject;
import org.junit.Assert;
import org.junit.Test;

public class SimpleIT {

    /**
     * null -> A
     */
    @Test
    public void test1() {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
                        .build()).build())
                .taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build())
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
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
                        .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                        .build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build()).build()).build());

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
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
                        .addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
                        .addTaskChainCriteria("A", "B", "STOP").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).build());

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
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
                        .addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
                        .addTaskChainCriteria("A", "C", "STOP").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).build());

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
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
                        .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                        .addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

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
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
                        .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                        .addTaskChainCriteria("A", "B", "VERSB").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

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
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
                        .addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("C", "CTask")).build())
                        .addTaskChainCriteria("A", "B", "VERSB").addTaskChainCriteria("A", "C", "VERSC").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CTask", new MultiUpdateStatusTaskService("C")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSC", new VerifyCodeTaskService("VersC")).build()).build()).build());

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
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder()
                        .addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask").addNextStatusGraph("A", "ATask")).build())
                        .addTaskChainCriteria("A", "B", "VERSB").addTaskChainCriteria("A", "A", "VERSA=>CHANGE").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersB")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSA", new VerifyCodeTaskService("VersA")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

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
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class)
                        .statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "BTask")).build())
                        .addTaskChainCriteria("A", "B", "(CHANGE,DATE)=>VERSB").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService("B")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersB")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("DATE", new SetNowDateTaskService()).build()).build()).build());

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
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
                        .build()).build())
                .taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build())
                .build());

        ITaskCluster taskCluster = engine.startEngine((ITaskObject) null);

        Assert.assertNull(taskCluster);
    }

    @Test
    public void test10() {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
                        .build()).build())
                .taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build())
                .build());

        engine.startEngine((ITaskCluster) null);

        Assert.assertTrue(true);
    }

    /**
     * Test checkGraphCreated
     * <p>
     * null -> A
     */
    @Test
    public void test11() {
        MemoryTaskManagerReaderWriter memoryTaskManagerReaderWriter = new MemoryTaskManagerReaderWriter();

        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
                        .build()).build())
                .taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build())
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
     * Test bug on task service
     * <p>
     * null -> BUG -> A
     */
    @Test
    public void test12() {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
                        .addTaskChainCriteria(null, "A", "BUG").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BUG", new BugTaskService()).build()).build()).build());

        BusinessObject businessObject = new BusinessObject();
        Assert.assertNull(businessObject.getStatus());

        ITaskCluster taskCluster = engine.startEngine(businessObject);

        Assert.assertEquals(businessObject.getStatus(), null);
        Assert.assertFalse(taskCluster.isCheckArchived());
    }

    /**
     * TaskService return null
     * <p>
     * null -> NULL -> A
     */
    @Test
    public void test13() {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
                        .addTaskChainCriteria(null, "A", "NULL").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("NULL", new NullTaskService()).build()).build()).build());

        BusinessObject businessObject = new BusinessObject();
        Assert.assertNull(businessObject.getStatus());

        ITaskCluster taskCluster = engine.startEngine(businessObject);

        Assert.assertEquals(businessObject.getStatus(), null);
        Assert.assertFalse(taskCluster.isCheckArchived());
    }

    /**
     * TaskService not found
     * <p>
     * null -> A
     */
    @Test
    public void test14() {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "NOTFOUND").build())
                        .build()).build()).taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder().build()).build());

        BusinessObject businessObject = new BusinessObject();
        Assert.assertNull(businessObject.getStatus());

        ITaskCluster taskCluster = engine.startEngine(businessObject);

        Assert.assertEquals(businessObject.getStatus(), null);
        Assert.assertFalse(taskCluster.isCheckArchived());
    }

    /**
     * TaskService not found
     * <p>
     * null -> NOTFOUND -> A
     */
    @Test
    public void test15() {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
                        .addTaskChainCriteria(null, "A", "NOTFOUND").build()).build())
                .taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()).build())
                .build());

        BusinessObject businessObject = new BusinessObject();
        Assert.assertNull(businessObject.getStatus());

        ITaskCluster taskCluster = engine.startEngine(businessObject);

        Assert.assertEquals(businessObject.getStatus(), null);
        Assert.assertFalse(taskCluster.isCheckArchived());
    }

    /**
     * Test two parallel task service
     * <p>
     * null -> (CHANGE,STOP) -> A
     */
    @Test
    public void test16() {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
                        .addTaskChainCriteria(null, "A", "CHANGE,STOP").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersA")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build()).build()).build());

        BusinessObject businessObject = new BusinessObject();

        ITaskCluster taskCluster = engine.startEngine(businessObject);

        Assert.assertEquals(businessObject.getStatus(), null);
        Assert.assertTrue(!taskCluster.isCheckArchived());
    }

    /**
     * Test two parallel task service
     * <p>
     * null -> (CHANGE1=>CHANGE2,VERSA=>CHANGE3) -> A
     */
    @Test
    public void test17() {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(
                TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build())
                        .addTaskChainCriteria(null, "A", "(CHANGE1=>CHANGE2)=>(VERSA=>CHANGE3)").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CHANGE1", new ChangeCodeTaskService("VersB")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CHANGE2", new ChangeCodeTaskService("VersA")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CHANGE3", new ChangeCodeTaskService("VersC")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSA", new VerifyCodeTaskService("VersA")).build()).build()).build());

        BusinessObject businessObject = new BusinessObject();

        ITaskCluster taskCluster = engine.startEngine(businessObject);

        Assert.assertEquals(businessObject.getStatus(), "A");
        Assert.assertEquals(businessObject.getCode(), "VersC");
        Assert.assertTrue(taskCluster.isCheckArchived());
    }
}
