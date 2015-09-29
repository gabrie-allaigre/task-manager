package com.synaptix.taskmanager.example.component;

import java.util.UUID;

import com.synaptix.taskmanager.component.ComponentInstanceToClass;
import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.listener.LogTaskCycleListener;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.SubTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.StatusTaskDefinitionBuilder;
import com.synaptix.taskmanager.example.component.business.CustomerOrderBuilder;
import com.synaptix.taskmanager.example.component.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.component.business.ICustomerOrder;
import com.synaptix.taskmanager.example.component.task.ChangeCodeTaskService;
import com.synaptix.taskmanager.example.component.task.MultiUpdateStatusTaskService;
import com.synaptix.taskmanager.example.component.task.VerifyCodeTaskService;

public class MainComponent3 {

	public static void main(String[] args) {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(
				TaskObjectManagerRegistryBuilder.newBuilder().instanceToClass(ComponentInstanceToClass.INSTANCE).addTaskObjectManager(
						TaskObjectManagerBuilder.<CustomerOrderStatus, ICustomerOrder>newBuilder(ICustomerOrder.class).statusGraphs(StatusGraphsBuilder.<CustomerOrderStatus>newBuilder()
								.addNextStatusGraph(CustomerOrderStatus.TCO, "ATask",
										StatusGraphsBuilder.<CustomerOrderStatus>newBuilder().addNextStatusGraph(CustomerOrderStatus.VAL, "BTask").addNextStatusGraph(CustomerOrderStatus.TCO, "ATask"))
								.build()).addTaskChainCriteria(CustomerOrderStatus.TCO, CustomerOrderStatus.VAL, "VERSB")
								.addTaskChainCriteria(CustomerOrderStatus.TCO, CustomerOrderStatus.TCO, "VERSA=>CHANGE").build()).build()).taskDefinitionRegistry(
				TaskDefinitionRegistryBuilder.newBuilder()
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService(CustomerOrderStatus.TCO)).build())
						.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService(CustomerOrderStatus.VAL)).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersB")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSA", new VerifyCodeTaskService("VersA")).build())
						.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build()).build());

		engine.addTaskManagerListener(new LogTaskCycleListener());

		ICustomerOrder customerOrder = new CustomerOrderBuilder().id(UUID.randomUUID()).version(0).customerOrderNo("VersA").confirmed(false).build();

		engine.startEngine(customerOrder);

	}
}
