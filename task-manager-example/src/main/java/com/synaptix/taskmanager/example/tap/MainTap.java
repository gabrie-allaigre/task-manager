package com.synaptix.taskmanager.example.tap;

import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.TaskDefinitionBuilder;
import com.synaptix.taskmanager.example.tap.model.Order;
import com.synaptix.taskmanager.example.tap.model.OrderStatus;
import com.synaptix.taskmanager.example.tap.task.EtudeStatusTaskService;
import com.synaptix.taskmanager.example.tap.task.TermineStatusTaskService;
import com.synaptix.taskmanager.example.tap.task.ValideStatusTaskService;
import com.synaptix.taskmanager.jpa.JPATaskFactory;
import com.synaptix.taskmanager.jpa.JPATaskManagerReaderWriter;

import javax.persistence.EntityManager;
import java.util.List;

public class MainTap {

    public static void main(String[] args) {
        TapHelper.getInstance().getJpaAccess().start();

        List<IStatusGraph<String>> statusGraphs = StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph(OrderStatus.ETUDE.name(), "ETUDE_TASK", StatusGraphsBuilder.<String>newBuilder()
                .addNextStatusGraph(OrderStatus.VALIDE.name(), "VALIDE_TASK", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph(OrderStatus.TERMINE.name(), "TERMINE_TASK"))).build();

        ITaskObjectManager<String, Order> orderTaskObjectManager = TaskObjectManagerBuilder.<String, Order>newBuilder(Order.class).statusGraphs(statusGraphs).build();

        ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(orderTaskObjectManager).build();

        ITaskDefinitionRegistry taskDefinitionRegistry = TaskDefinitionRegistryBuilder.newBuilder()
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("ETUDE_TASK", new EtudeStatusTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VALIDE_TASK", new ValideStatusTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("TERMINE_TASK", new TermineStatusTaskService()).build()).build();

        JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(TapHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

        ITaskManagerConfiguration taskManagerConfiguration = TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(taskObjectManagerRegistry)
                .taskDefinitionRegistry(taskDefinitionRegistry).taskFactory(new JPATaskFactory()).taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build();
        TaskManagerEngine engine = new TaskManagerEngine(taskManagerConfiguration);

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        em.getTransaction().begin();

        Order order = new Order();
        em.persist(order);

        em.getTransaction().commit();

        engine.startEngine(order);

        System.out.println(order);

        TapHelper.getInstance().getJpaAccess().stop();
    }

}
