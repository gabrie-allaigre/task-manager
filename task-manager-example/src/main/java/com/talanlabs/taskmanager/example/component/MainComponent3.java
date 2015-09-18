package com.talanlabs.taskmanager.example.component;

import com.talanlabs.taskmanager.component.ComponentInstanceToClass;
import com.talanlabs.taskmanager.engine.TaskManagerEngine;
import com.talanlabs.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.talanlabs.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.talanlabs.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.talanlabs.taskmanager.engine.graph.StatusGraphsBuilder;
import com.talanlabs.taskmanager.engine.listener.LogTaskCycleListener;
import com.talanlabs.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.talanlabs.taskmanager.engine.taskdefinition.TaskDefinitionBuilder;
import com.talanlabs.taskmanager.example.component.business.CustomerOrderBuilder;
import com.talanlabs.taskmanager.example.component.business.CustomerOrderStatus;
import com.talanlabs.taskmanager.example.component.business.ICustomerOrder;
import com.talanlabs.taskmanager.example.component.task.ChangeCodeTaskService;
import com.talanlabs.taskmanager.example.component.task.MultiUpdateStatusTaskService;
import com.talanlabs.taskmanager.example.component.task.VerifyCodeTaskService;

import java.util.UUID;

public class MainComponent3 {

    public static void main(String[] args) {
        TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(
                TaskObjectManagerRegistryBuilder.newBuilder().instanceToClass(ComponentInstanceToClass.INSTANCE).addTaskObjectManager(
                        TaskObjectManagerBuilder.<CustomerOrderStatus, ICustomerOrder>newBuilder(ICustomerOrder.class).statusGraphs(StatusGraphsBuilder.<CustomerOrderStatus>newBuilder()
                                .addNextStatusGraph(CustomerOrderStatus.TCO, "ATask",
                                        StatusGraphsBuilder.<CustomerOrderStatus>newBuilder().addNextStatusGraph(CustomerOrderStatus.VAL, "BTask").addNextStatusGraph(CustomerOrderStatus.TCO, "ATask"))
                                .build()).addTaskChainCriteria(CustomerOrderStatus.TCO, CustomerOrderStatus.VAL, "VERSB")
                                .addTaskChainCriteria(CustomerOrderStatus.TCO, CustomerOrderStatus.TCO, "VERSA=>CHANGE").build()).build()).taskDefinitionRegistry(
                TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService(CustomerOrderStatus.TCO)).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService(CustomerOrderStatus.VAL)).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersB")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSA", new VerifyCodeTaskService("VersA")).build())
                        .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

        engine.addTaskManagerListener(new LogTaskCycleListener());

        ICustomerOrder customerOrder = CustomerOrderBuilder.newBuilder().id(UUID.randomUUID().toString()).version(0).customerOrderNo("VersA").confirmed(false).build();

        engine.startEngine(customerOrder);

    }
}
