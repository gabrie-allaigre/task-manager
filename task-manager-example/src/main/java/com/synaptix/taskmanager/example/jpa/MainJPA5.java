package com.synaptix.taskmanager.example.jpa;

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
import com.synaptix.taskmanager.example.jpa.model.Todo;
import com.synaptix.taskmanager.example.jpa.task.MultiUpdateStatusTaskService;
import com.synaptix.taskmanager.example.jpa.task.SetNameTaskService;
import com.synaptix.taskmanager.example.jpa.task.StopTaskService;
import com.synaptix.taskmanager.example.jpa.task.VerifyNameTaskService;
import com.synaptix.taskmanager.jpa.JPATaskFactory;
import com.synaptix.taskmanager.jpa.JPATaskManagerReaderWriter;
import com.synaptix.taskmanager.jpa.model.Cluster;
import com.synaptix.taskmanager.jpa.model.Task;

import javax.persistence.Query;
import java.util.List;

/**
 * Remove a task object
 */
public class MainJPA5 {

    public static void main(String[] args) throws Exception {
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

        Todo todo1 = new Todo();
        todo1.setSummary("Fiche de bug");
        JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(todo1);

        Todo todo2 = new Todo();
        todo2.setSummary("Fiche de story");
        JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(todo2);

        JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

        engine.startEngine(todo1, todo2);

        engine.removeTaskObjectsFromTaskCluster(todo2);

        //todo2.setName("Laureline");

        //engine.startEngine(todo2);

        showClusters();
        showTodos();
        showTasks();

        JPAHelper.getInstance().getJpaAccess().getEntityManager().close();
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
