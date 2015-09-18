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
import com.talanlabs.taskmanager.engine.listener.LogTaskCycleListener;
import com.talanlabs.taskmanager.engine.manager.ITaskObjectManager;
import com.talanlabs.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.talanlabs.taskmanager.engine.taskdefinition.TaskDefinitionBuilder;
import com.talanlabs.taskmanager.example.component.business.CustomerOrderBuilder;
import com.talanlabs.taskmanager.example.component.business.CustomerOrderStatus;
import com.talanlabs.taskmanager.example.component.business.ICustomerOrder;
import com.talanlabs.taskmanager.example.component.task.enrichment.DateClosedTaskService;
import com.talanlabs.taskmanager.example.component.task.enrichment.NotConfirmedTaskService;
import com.talanlabs.taskmanager.example.component.task.enrichment.ReferenceTaskService;
import com.talanlabs.taskmanager.example.component.task.updatestatus.CANTaskService;
import com.talanlabs.taskmanager.example.component.task.updatestatus.CLOTaskService;
import com.talanlabs.taskmanager.example.component.task.updatestatus.TCOTaskService;
import com.talanlabs.taskmanager.example.component.task.updatestatus.VALTaskService;

import java.util.List;
import java.util.UUID;

public class MainComponent2 {

    public static void main(String[] args) {
        List<IStatusGraph<CustomerOrderStatus>> statusGraphs = StatusGraphsBuilder.<CustomerOrderStatus>newBuilder().addNextStatusGraph(CustomerOrderStatus.TCO, "TCO",
                StatusGraphsBuilder.<CustomerOrderStatus>newBuilder().addNextStatusGraph(CustomerOrderStatus.VAL, "VAL",
                        StatusGraphsBuilder.<CustomerOrderStatus>newBuilder().addNextStatusGraph(CustomerOrderStatus.TCO, "TCO").addNextStatusGraph(CustomerOrderStatus.CLO, "CLO"))
                        .addNextStatusGraph(CustomerOrderStatus.CAN, "CAN")).addNextStatusGraph(CustomerOrderStatus.CAN, "CAN").build();

        ITaskObjectManager<CustomerOrderStatus, ICustomerOrder> customerOrderTaskObjectManager = TaskObjectManagerBuilder.<CustomerOrderStatus, ICustomerOrder>newBuilder(ICustomerOrder.class)
                .statusGraphs(statusGraphs).addTaskChainCriteria(null, CustomerOrderStatus.TCO, "REF").addTaskChainCriteria(CustomerOrderStatus.VAL, CustomerOrderStatus.CLO, "REF2=>DATE")
                .addTaskChainCriteria(CustomerOrderStatus.VAL, CustomerOrderStatus.TCO, "NOT-VAL").build();

        ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().instanceToClass(ComponentInstanceToClass.INSTANCE)
                .addTaskObjectManager(customerOrderTaskObjectManager).build();

        ITaskDefinitionRegistry taskDefinitionRegistry = TaskDefinitionRegistryBuilder.newBuilder().addTaskDefinition(TaskDefinitionBuilder.newBuilder("TCO", new TCOTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VAL", new VALTaskService()).build()).addTaskDefinition(TaskDefinitionBuilder.newBuilder("CLO", new CLOTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CAN", new CANTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("DATE", new DateClosedTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("REF", new ReferenceTaskService("Ma ref 1")).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("REF2", new ReferenceTaskService("Ma ref 2")).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("NOT-VAL", new NotConfirmedTaskService()).build()).build();

        TaskManagerEngine engine = new TaskManagerEngine(
                TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(taskObjectManagerRegistry).taskDefinitionRegistry(taskDefinitionRegistry).build());

        engine.addTaskManagerListener(new LogTaskCycleListener());

        ICustomerOrder customerOrder = CustomerOrderBuilder.newBuilder().id(UUID.randomUUID().toString()).version(0).customerOrderNo("123456").confirmed(false).build();
        engine.startEngine(customerOrder);

        System.out.println(customerOrder);
        customerOrder.setConfirmed(true);

        engine.startEngine(customerOrder);

        System.out.println(customerOrder);

        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        engine.startEngine(customerOrder);

        System.out.println(customerOrder);

    }
}
