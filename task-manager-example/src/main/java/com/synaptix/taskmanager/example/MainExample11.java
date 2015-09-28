package com.synaptix.taskmanager.example;

import java.util.UUID;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.Proxy;
import com.synaptix.taskmanager.component.ComponentInstanceToClass;
import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.listener.LogTaskCycleListener;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.NormalTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.UpdateStatusTaskDefinitionBuilder;
import com.synaptix.taskmanager.example.business.CustomerOrderBuilder;
import com.synaptix.taskmanager.example.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.business.ICustomerOrder;
import com.synaptix.taskmanager.example.tasks.ChangeCodeTaskService;
import com.synaptix.taskmanager.example.tasks.MultiUpdateStatusTaskService;
import com.synaptix.taskmanager.example.tasks.VerifyCodeTaskService;
import com.synaptix.taskmanager.model.ITaskObject;

public class MainExample11 {

	public static void main(String[] args) {
		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder()
				.taskObjectManagerRegistry(TaskObjectManagerRegistryBuilder.newBuilder().instanceToClass(ComponentInstanceToClass.INSTANCE).addTaskObjectManager(TaskObjectManagerBuilder.<CustomerOrderStatus,ICustomerOrder>newBuilder(ICustomerOrder.class).statusGraphs(StatusGraphsBuilder.<CustomerOrderStatus> newBuilder()
						.addNextStatusGraph(CustomerOrderStatus.TCO, "ATask",
								StatusGraphsBuilder.<CustomerOrderStatus> newBuilder().addNextStatusGraph(CustomerOrderStatus.VAL, "BTask").addNextStatusGraph(CustomerOrderStatus.TCO,
										"ATask"))
						.build()).addTaskChainCriteria(CustomerOrderStatus.TCO, CustomerOrderStatus.VAL, "VERSB")
						.addTaskChainCriteria(CustomerOrderStatus.TCO, CustomerOrderStatus.TCO, "VERSA=>CHANGE").build()).build())
				.taskDefinitionRegistry(TaskDefinitionRegistryBuilder.newBuilder()
						.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService(CustomerOrderStatus.TCO)).build())
						.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("BTask", new MultiUpdateStatusTaskService(CustomerOrderStatus.VAL)).build())
						.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("CHANGE", new ChangeCodeTaskService("VersB")).build())
						.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("VERSA", new VerifyCodeTaskService("VersA")).build())
						.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("VERSB", new VerifyCodeTaskService("VersB")).build()).build())
				.build());

		engine.addTaskManagerListener(new LogTaskCycleListener());

		ICustomerOrder customerOrder = new CustomerOrderBuilder().id(UUID.randomUUID()).version(0).customerOrderNo("VersA").confirmed(false).build();

		engine.startEngine(customerOrder);

	}
}
