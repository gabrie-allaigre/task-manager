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
import com.synaptix.taskmanager.example.tap.model.FicheContact;
import com.synaptix.taskmanager.example.tap.model.FicheContactStatus;
import com.synaptix.taskmanager.example.tap.model.Item;
import com.synaptix.taskmanager.example.tap.task.EtudeStatusTaskService;
import com.synaptix.taskmanager.example.tap.task.TermineStatusTaskService;
import com.synaptix.taskmanager.example.tap.task.TestItemTaskService;
import com.synaptix.taskmanager.example.tap.task.ValideStatusTaskService;
import com.synaptix.taskmanager.jpa.JPATaskFactory;
import com.synaptix.taskmanager.jpa.JPATaskManagerReaderWriter;
import com.synaptix.taskmanager.jpa.model.Task;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class MainTap {

    public static void main(String[] args) {
        TapHelper.getInstance().getJpaAccess().start();

        List<IStatusGraph<FicheContactStatus>> statusGraphs = StatusGraphsBuilder.<FicheContactStatus>newBuilder().addNextStatusGraph(FicheContactStatus.ETUDE, "ETUDE_TASK",
                StatusGraphsBuilder.<FicheContactStatus>newBuilder().addNextStatusGraph(FicheContactStatus.VALIDE, "VALIDE_TASK",
                        StatusGraphsBuilder.<FicheContactStatus>newBuilder().addNextStatusGraph(FicheContactStatus.TERMINE, "TERMINE_TASK"))).build();

        ITaskObjectManager<FicheContactStatus, FicheContact> orderTaskObjectManager = TaskObjectManagerBuilder.<FicheContactStatus, FicheContact>newBuilder(FicheContact.class)
                .statusGraphs(statusGraphs).addTaskChainCriteria(FicheContactStatus.ETUDE, FicheContactStatus.VALIDE, "TEST_TASK").build();

        ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(orderTaskObjectManager).build();

        ITaskDefinitionRegistry taskDefinitionRegistry = TaskDefinitionRegistryBuilder.newBuilder()
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("ETUDE_TASK", new EtudeStatusTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VALIDE_TASK", new ValideStatusTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("TERMINE_TASK", new TermineStatusTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("TEST_TASK", new TestItemTaskService()).build()).build();

        JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(TapHelper.getInstance().getJpaAccess(), JPATaskManagerReaderWriter.RemoveMode.DELETE);

        ITaskManagerConfiguration taskManagerConfiguration = TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(taskObjectManagerRegistry)
                .taskDefinitionRegistry(taskDefinitionRegistry).taskFactory(new JPATaskFactory()).taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build();
        TaskManagerEngine engine = new TaskManagerEngine(taskManagerConfiguration);

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        em.getTransaction().begin();

        FicheContact ficheContact = new FicheContact();
        em.persist(ficheContact);

        em.getTransaction().commit();

        engine.startEngine(ficheContact);

        System.out.println(ficheContact);

        showItems();

        TapHelper.getInstance().getJpaAccess().stop();
    }

    private static void showItems() {
        System.out.println("------ Items ------");
        Query q = TapHelper.getInstance().getJpaAccess().getEntityManager().createQuery("select t from Item t");
        List<Item> items = q.getResultList();
        items.forEach(System.out::println);
        System.out.println("Size: " + items.size());
    }
}
