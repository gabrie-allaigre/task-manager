package com.talanlabs.taskmanager.engine.test.unit;

import com.talanlabs.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.talanlabs.taskmanager.engine.configuration.xml.XMLParseException;
import com.talanlabs.taskmanager.engine.configuration.xml.XMLTaskManagerConfigurationBuilder;
import com.talanlabs.taskmanager.engine.graph.IStatusGraph;
import com.talanlabs.taskmanager.engine.test.data.BusinessObject;
import com.talanlabs.taskmanager.engine.test.data.MultiParamsTaskService;
import com.talanlabs.taskmanager.engine.test.data.MultiUpdateMyStatusObjectTaskService;
import com.talanlabs.taskmanager.engine.test.data.MultiUpdateStatusTaskService;
import com.talanlabs.taskmanager.engine.test.data.MyStatus;
import com.talanlabs.taskmanager.engine.test.data.MyStatusObject;
import com.talanlabs.taskmanager.engine.test.data.OtherBusinessObject;
import com.talanlabs.taskmanager.engine.test.data.StopTaskService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class XMLTaskManagerConfigurationTest {

    /**
     * StatusGraph simple
     */
    @Test
    public void test1() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config1.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);
        Assert.assertNotNull(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class));

        List<IStatusGraph<String>> statusGraphs = taskManagerConfiguration.getTaskObjectManagerRegistry().<String, BusinessObject>getTaskObjectManager(BusinessObject.class)
                .getNextStatusGraphsByTaskObjectType(null, null);
        Assert.assertEquals(statusGraphs.size(), 1);

        StatusGraphTest.assertUniqueContains(statusGraphs, null, "A", "A_TASK");
    }

    /**
     * StatusGraph complex
     */
    @Test
    public void test2() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config2.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);
        Assert.assertNotNull(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class));

        List<IStatusGraph<String>> statusGraphs1 = taskManagerConfiguration.getTaskObjectManagerRegistry().<String, BusinessObject>getTaskObjectManager(BusinessObject.class)
                .getNextStatusGraphsByTaskObjectType(null, null);
        Assert.assertEquals(statusGraphs1.size(), 2);

        StatusGraphTest.assertUniqueContains(statusGraphs1, null, "A", "A_TASK");
        StatusGraphTest.assertUniqueContains(statusGraphs1, null, "C", "C_TASK");

        List<IStatusGraph<String>> statusGraphs2 = taskManagerConfiguration.getTaskObjectManagerRegistry().<String, BusinessObject>getTaskObjectManager(BusinessObject.class)
                .getNextStatusGraphsByTaskObjectType(null, "A");
        Assert.assertEquals(statusGraphs2.size(), 2);

        StatusGraphTest.assertUniqueContains(statusGraphs2, "A", "B", "B_TASK");
        StatusGraphTest.assertUniqueContains(statusGraphs2, "A", "E", "E_TASK");

        List<IStatusGraph<String>> statusGraphs3 = taskManagerConfiguration.getTaskObjectManagerRegistry().<String, BusinessObject>getTaskObjectManager(BusinessObject.class)
                .getNextStatusGraphsByTaskObjectType(null, "E");
        Assert.assertEquals(statusGraphs3.size(), 1);
        StatusGraphTest.assertUniqueContains(statusGraphs3, "E", "F", "F_TASK");
    }

    /**
     * Status is not String
     */
    @Test
    public void test3() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config3.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);
        Assert.assertNotNull(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class));

        List<IStatusGraph<MyStatus>> statusGraphs = taskManagerConfiguration.getTaskObjectManagerRegistry().<MyStatus, BusinessObject>getTaskObjectManager(BusinessObject.class)
                .getNextStatusGraphsByTaskObjectType(null, null);
        Assert.assertEquals(statusGraphs.size(), 1);

        Assert.assertEquals(statusGraphs.get(0).getCurrentStatus().getClass(), MyStatus.class);

        StatusGraphTest.assertUniqueContains(statusGraphs, null, MyStatus.A, "A_TASK");
    }

    /**
     * IGetStatus
     */
    @Test
    public void test4() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config4.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);
        Assert.assertNotNull(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class));

        BusinessObject bo = new BusinessObject();
        bo.setStatus("B");

        String status = taskManagerConfiguration.getTaskObjectManagerRegistry().<String, BusinessObject>getTaskObjectManager(BusinessObject.class).getInitialStatus(bo);
        Assert.assertEquals(status, bo.getStatus());
    }

    /**
     * InstanceToClass
     */
    @Test
    public void test5() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config5.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);
        Assert.assertNotNull(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class));

        OtherBusinessObject bo = new OtherBusinessObject();
        Assert.assertEquals(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(bo),
                taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class));
    }

    /**
     * Multiple graph
     */
    @Test
    public void test6() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config6.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);
        Assert.assertNotNull(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class));
        Assert.assertNotNull(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(OtherBusinessObject.class));
        Assert.assertNotSame(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class),
                taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(OtherBusinessObject.class));

        List<IStatusGraph<String>> statusGraphs1 = taskManagerConfiguration.getTaskObjectManagerRegistry().<String, BusinessObject>getTaskObjectManager(BusinessObject.class)
                .getNextStatusGraphsByTaskObjectType(null, null);
        Assert.assertEquals(statusGraphs1.size(), 1);

        StatusGraphTest.assertUniqueContains(statusGraphs1, null, "A", "A_TASK");

        List<IStatusGraph<String>> statusGraphs2 = taskManagerConfiguration.getTaskObjectManagerRegistry().<String, OtherBusinessObject>getTaskObjectManager(OtherBusinessObject.class)
                .getNextStatusGraphsByTaskObjectType(null, null);
        Assert.assertEquals(statusGraphs2.size(), 1);

        StatusGraphTest.assertUniqueContains(statusGraphs2, null, "B", "B_TASK");
    }

    /**
     * Transition
     */
    @Test
    public void test7() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config7.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);
        Assert.assertNotNull(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class));

        Assert.assertEquals(taskManagerConfiguration.getTaskObjectManagerRegistry().<String, BusinessObject>getTaskObjectManager(BusinessObject.class).getTaskChainCriteria(null, null, "A"), "CHANGE");
        Assert.assertEquals(taskManagerConfiguration.getTaskObjectManagerRegistry().<String, BusinessObject>getTaskObjectManager(BusinessObject.class).getTaskChainCriteria(null, "B", "D"),
                "CODE=>VERSD");
    }

    /**
     * Transition
     */
    @Test
    public void test8() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config8.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);
        Assert.assertNotNull(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class));

        Assert.assertEquals(taskManagerConfiguration.getTaskObjectManagerRegistry().<MyStatus, BusinessObject>getTaskObjectManager(BusinessObject.class).getTaskChainCriteria(null, null, MyStatus.A),
                "CHANGE");
        Assert.assertEquals(
                taskManagerConfiguration.getTaskObjectManagerRegistry().<MyStatus, BusinessObject>getTaskObjectManager(BusinessObject.class).getTaskChainCriteria(null, MyStatus.B, MyStatus.D),
                "CODE=>VERSD");
    }

    /**
     * Transition without parameter
     */
    @Test
    public void test9() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config9.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);

        Assert.assertNotNull(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("STOP"));
        Assert.assertTrue(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("STOP").getTaskService() instanceof StopTaskService);
    }

    /**
     * Transition with parameter
     */
    @Test
    public void test10() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config10.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);

        Assert.assertNotNull(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK"));
        Assert.assertTrue(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK").getTaskService() instanceof MultiUpdateStatusTaskService);

        MultiUpdateStatusTaskService multiUpdateStatusTaskService = (MultiUpdateStatusTaskService) taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK").getTaskService();
        Assert.assertEquals(multiUpdateStatusTaskService.getStatus(), "A");
    }

    /**
     * Transition with parameters
     */
    @Test
    public void test11() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config11.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);

        Assert.assertNotNull(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK"));
        Assert.assertTrue(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK").getTaskService() instanceof MultiParamsTaskService);

        MultiParamsTaskService multiParamsTaskService = (MultiParamsTaskService) taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK").getTaskService();
        Assert.assertEquals(multiParamsTaskService.getNewCode(), "A");
        Assert.assertEquals(multiParamsTaskService.getEntier(), 10);
        Assert.assertEquals(multiParamsTaskService.getRien(), 20.5f);
        Assert.assertEquals(multiParamsTaskService.getStatus(), MyStatus.B);
    }

    /**
     * Transition with parameters
     */
    @Test
    public void test12() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config12.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);

        Assert.assertNotNull(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK"));
        Assert.assertTrue(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK").getTaskService() instanceof MultiParamsTaskService);

        MultiParamsTaskService multiParamsTaskService = (MultiParamsTaskService) taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK").getTaskService();
        Assert.assertEquals(multiParamsTaskService.getNewCode(), "A");
        Assert.assertEquals(multiParamsTaskService.getEntier(), 10);
        Assert.assertEquals(multiParamsTaskService.getRien(), 20.5f);
        Assert.assertNull(multiParamsTaskService.getStatus());
        Assert.assertEquals(multiParamsTaskService.getD(), 12.4564644);
    }

    /**
     * Transition with parameters, error multi constructor
     */
    @Test
    public void test13() {
        ITaskManagerConfiguration taskManagerConfiguration;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config13.xml")).build();
        } catch (XMLParseException e) {
            return;
        }

        Assert.assertNull(taskManagerConfiguration);
    }

    @Test
    public void test14() {
        ITaskManagerConfiguration taskManagerConfiguration = null;
        try {
            taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config14.xml")).build();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(taskManagerConfiguration);

        Assert.assertNotNull(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK"));
        Assert.assertNotNull(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("B_TASK"));

        Assert.assertTrue(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK").getTaskService() instanceof MultiUpdateMyStatusObjectTaskService);
        Assert.assertTrue(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("B_TASK").getTaskService() instanceof MultiUpdateMyStatusObjectTaskService);

        Assert.assertEquals(((MultiUpdateMyStatusObjectTaskService) taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK").getTaskService()).getStatus(), MyStatusObject.A);
        Assert.assertEquals(((MultiUpdateMyStatusObjectTaskService) taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("B_TASK").getTaskService()).getStatus(), MyStatusObject.B);
    }
}
