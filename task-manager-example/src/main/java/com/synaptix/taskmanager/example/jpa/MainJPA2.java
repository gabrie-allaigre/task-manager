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
import com.synaptix.taskmanager.engine.taskdefinition.StatusTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.SubTaskDefinitionBuilder;
import com.synaptix.taskmanager.example.jpa.model.Cluster;
import com.synaptix.taskmanager.example.jpa.model.Task;
import com.synaptix.taskmanager.example.jpa.model.Todo;
import com.synaptix.taskmanager.example.jpa.task.MultiUpdateStatusTaskService;
import com.synaptix.taskmanager.example.jpa.task.SetSummaryTaskService;

import javax.persistence.Query;
import java.util.List;

public class MainJPA2 {

	public static void main(String[] args) throws Exception {
		List<IStatusGraph<String>> statusGraphs = StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "A_TASK", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("B", "B_TASK"))
				.build();

		ITaskObjectManager<String, Todo> todoTaskObjectManager = TaskObjectManagerBuilder.<String, Todo>newBuilder(Todo.class).statusGraphs(statusGraphs).addTaskChainCriteria(null, "A", "GABY").addTaskChainCriteria("A", "B", "SANDRA").build();

		ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(todoTaskObjectManager).build();

		ITaskDefinitionRegistry taskDefinitionRegistry = TaskDefinitionRegistryBuilder.newBuilder()
				.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("A_TASK", new MultiUpdateStatusTaskService("A")).build())
				.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("B_TASK", new MultiUpdateStatusTaskService("B")).build())
				.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("GABY", new SetSummaryTaskService("GABY")).build())
				.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("SANDRA", new SetSummaryTaskService("SANDRA")).build())
				.build();

		JPATaskManagerReaderWriter jpaTaskManagerReaderWriter = new JPATaskManagerReaderWriter(taskDefinitionRegistry);

		ITaskManagerConfiguration taskManagerConfiguration = TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(taskObjectManagerRegistry)
				.taskDefinitionRegistry(taskDefinitionRegistry).taskFactory(new JPATaskFactory()).taskManagerReader(jpaTaskManagerReaderWriter).taskManagerWriter(jpaTaskManagerReaderWriter).build();
		TaskManagerEngine engine = new TaskManagerEngine(taskManagerConfiguration);

		Todo todo = new Todo();
		JPAHelper.getInstance().getEntityManager().persist(todo);

		engine.startEngine(todo);

		showClusters();
		showTodos();
		showTasks();

		// read the existing entries and write to console

		JPAHelper.getInstance().getEntityManager().close();
	}

	private static void showClusters() {
		System.out.println("------ Cluster ------");
		Query q = JPAHelper.getInstance().getEntityManager().createQuery("select t from Cluster t");
		List<Cluster> clusters = q.getResultList();
		for (Cluster t : clusters) {
			System.out.println(t);
		}
		System.out.println("Size: " + clusters.size());
	}

	private static void showTodos() {
		System.out.println("------ Todo ------");
		Query q = JPAHelper.getInstance().getEntityManager().createQuery("select t from Todo t");
		List<Todo> todos = q.getResultList();
		for (Todo t : todos) {
			System.out.println(t);
		}
		System.out.println("Size: " + todos.size());
	}

	private static void showTasks() {
		System.out.println("------ Task ------");
		Query q = JPAHelper.getInstance().getEntityManager().createQuery("select t from Task t");
		List<Task> tasks = q.getResultList();
		for (Task t : tasks) {
			System.out.println(t);
		}
		System.out.println("Size: " + tasks.size());
	}
}
