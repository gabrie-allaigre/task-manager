package com.talanlabs.taskmanager.example.component;

import com.talanlabs.taskmanager.component.ComponentInstanceToClass;
import com.talanlabs.taskmanager.engine.TaskManagerEngine;
import com.talanlabs.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.talanlabs.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.talanlabs.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.talanlabs.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.talanlabs.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.talanlabs.taskmanager.engine.graph.IStatusGraph;
import com.talanlabs.taskmanager.engine.graph.StatusGraphsBuilder;
import com.talanlabs.taskmanager.engine.manager.ITaskObjectManager;
import com.talanlabs.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.talanlabs.taskmanager.engine.taskdefinition.TaskDefinitionBuilder;
import com.talanlabs.taskmanager.example.component.business.CustomerOrderBuilder;
import com.talanlabs.taskmanager.example.component.business.CustomerOrderStatus;
import com.talanlabs.taskmanager.example.component.business.ICustomerOrder;
import com.talanlabs.taskmanager.example.component.task.MultiUpdateStatusTaskService;

import java.util.List;
import java.util.UUID;

public class MainComponent1 {

    public static void main(String[] args) {
        List<IStatusGraph<CustomerOrderStatus>> statusGraphs = StatusGraphsBuilder.<CustomerOrderStatus>newBuilder().addNextStatusGraph(CustomerOrderStatus.TCO, "TCO_TASK").build();

        ITaskObjectManager<CustomerOrderStatus, ICustomerOrder> customerOrderTaskObjectManager = TaskObjectManagerBuilder.<CustomerOrderStatus, ICustomerOrder>newBuilder(ICustomerOrder.class)
                .statusGraphs(statusGraphs).build();

        ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().instanceToClass(ComponentInstanceToClass.INSTANCE)
                .addTaskObjectManager(customerOrderTaskObjectManager).build();

        ITaskDefinitionRegistry taskDefinitionRegistry = TaskDefinitionRegistryBuilder.newBuilder()
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("TCO_TASK", new MultiUpdateStatusTaskService(CustomerOrderStatus.TCO)).build()).build();

        TaskManagerEngine engine = new TaskManagerEngine(
                TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(taskObjectManagerRegistry).taskDefinitionRegistry(taskDefinitionRegistry).build());

        ICustomerOrder customerOrder = CustomerOrderBuilder.newBuilder().id(UUID.randomUUID().toString()).version(0).customerOrderNo("123456").confirmed(false).build();
        engine.startEngine(customerOrder);
    }
}
