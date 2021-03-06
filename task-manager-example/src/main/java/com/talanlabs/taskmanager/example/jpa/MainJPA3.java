package com.talanlabs.taskmanager.example.jpa;

import com.talanlabs.taskmanager.engine.TaskManagerEngine;
import com.talanlabs.taskmanager.engine.configuration.ITaskManagerConfiguration;
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
import com.talanlabs.taskmanager.example.jpa.model.Todo;
import com.talanlabs.taskmanager.example.jpa.task.MultiUpdateStatusTaskService;
import com.talanlabs.taskmanager.example.jpa.task.SetNameTaskService;
import com.talanlabs.taskmanager.example.jpa.task.StopTaskService;
import com.talanlabs.taskmanager.example.jpa.task.VerifyNameTaskService;
import com.talanlabs.taskmanager.jpa.JPATaskFactory;
import com.talanlabs.taskmanager.jpa.JPATaskManagerReaderWriter;
import com.talanlabs.taskmanager.jpa.model.Cluster;
import com.talanlabs.taskmanager.jpa.model.Task;

import javax.persistence.Query;
import java.util.List;

public class MainJPA3 {

    public static void main(String[] args) throws Exception {
        JPAHelper.getInstance().getJpaAccess().start();

        List<IStatusGraph<String>> statusGraphs = StatusGraphsBuilder.<String>newBuilder()
                .addNextStatusGraph("A", "A_TASK", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "B_TASK").addNextStatusGraph("C", "C_TASK").addNextStatusGraph("D", "D_TASK"))
                .build();

        ITaskObjectManager<String, Todo> todoTaskObjectManager = TaskObjectManagerBuilder.<String, Todo>newBuilder(Todo.class).statusGraphs(statusGraphs).addTaskChainCriteria(null, "A", "GABY")
                .addTaskChainCriteria("A", "B", "SANDRA=>VERIFY_LAURELINE").addTaskChainCriteria("A", "C", "STOP").addTaskChainCriteria("A", "D", "STOP").build();

        ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(todoTaskObjectManager).build();

        ITaskDefinitionRegistry taskDefinitionRegistry = TaskDefinitionRegistryBuilder.newBuilder()
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("A_TASK", new MultiUpdateStatusTaskService("A")).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("B_TASK", new MultiUpdateStatusTaskService("B")).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("C_TASK", new MultiUpdateStatusTaskService("C")).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("D_TASK", new MultiUpdateStatusTaskService("D")).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("GABY", new SetNameTaskService("Gaby")).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("SANDRA", new SetNameTaskService("Sandra")).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("STOP", new StopTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VERIFY_LAURELINE", new VerifyNameTaskService("Laureline")).build()).build();

        JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(JPAHelper.getInstance().getJpaAccess());

        ITaskManagerConfiguration taskManagerConfiguration = TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(taskObjectManagerRegistry)
                .taskDefinitionRegistry(taskDefinitionRegistry).taskFactory(new JPATaskFactory()).taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build();
        TaskManagerEngine engine = new TaskManagerEngine(taskManagerConfiguration);

        JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

        Query q = JPAHelper.getInstance().getJpaAccess().getEntityManager().createQuery("select t from Todo t");
        List<Todo> todos = q.getResultList();
        for (Todo todo : todos) {
            System.out.println(todo);

            todo.setName("Laureline");
            JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(todo);

            engine.startEngine(todo);
        }

        JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

        showClusters();
        showTodos();
        showTasks();

        // read the existing entries and write to console

        JPAHelper.getInstance().getJpaAccess().stop();
    }

    private static void showClusters() {
        System.out.println("------ Cluster ------");
        Query q = JPAHelper.getInstance().getJpaAccess().getEntityManager().createQuery("select t from Cluster t");
        List<Cluster> clusters = q.getResultList();
        clusters.forEach(System.out::println);
        System.out.println("Size: " + clusters.size());
    }

    private static void showTodos() {
        System.out.println("------ Todo ------");
        Query q = JPAHelper.getInstance().getJpaAccess().getEntityManager().createQuery("select t from Todo t");
        List<Todo> todos = q.getResultList();
        todos.forEach(System.out::println);
        System.out.println("Size: " + todos.size());
    }

    private static void showTasks() {
        System.out.println("------ Task ------");
        Query q = JPAHelper.getInstance().getJpaAccess().getEntityManager().createQuery("select t from Task t");
        List<Task> tasks = q.getResultList();
        tasks.forEach(System.out::println);
        System.out.println("Size: " + tasks.size());
    }
}
