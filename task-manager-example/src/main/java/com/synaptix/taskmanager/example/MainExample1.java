package com.synaptix.taskmanager.example;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.Proxy;
import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.UpdateStatusTaskDefinitionBuilder;
import com.synaptix.taskmanager.example.business.CustomerOrderBuilder;
import com.synaptix.taskmanager.example.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.business.ICustomerOrder;
import com.synaptix.taskmanager.example.tasks.MultiUpdateStatusTaskService;
import com.synaptix.taskmanager.model.ITaskObject;

import java.util.List;
import java.util.UUID;

public class MainExample1 {

	public static void main(String[] args) {
		List<IStatusGraph<CustomerOrderStatus>> statusGraphs = StatusGraphsBuilder.<CustomerOrderStatus>newBuilder().addNextStatusGraph(CustomerOrderStatus.TCO, "TCO_TASK").build();

		ITaskObjectManager<CustomerOrderStatus,ICustomerOrder> customerOrderTaskObjectManager = TaskObjectManagerBuilder.<CustomerOrderStatus,ICustomerOrder>newBuilder(ICustomerOrder.class).statusGraphs(statusGraphs).build();

		ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().getClass(new TaskObjectManagerRegistryBuilder.IGetClass() {
			@SuppressWarnings("unchecked")
			@Override
			public <F extends ITaskObject> Class<F> getClass(F taskObject) {
				if (taskObject instanceof IComponent) {
					return (Class<F>) ((Proxy) taskObject).straightGetComponentClass();
				}
				return (Class<F>) taskObject.getClass();
			}
		}).addTaskObjectManager(customerOrderTaskObjectManager).build();

		ITaskDefinitionRegistry taskDefinitionRegistry = TaskDefinitionRegistryBuilder.newBuilder()
				.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("TCO_TASK", new MultiUpdateStatusTaskService(CustomerOrderStatus.TCO)).build()).build();

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(taskObjectManagerRegistry)
				.taskDefinitionRegistry(taskDefinitionRegistry).build());

		ICustomerOrder customerOrder = new CustomerOrderBuilder().id(UUID.randomUUID()).version(0).customerOrderNo("123456").confirmed(false).build();
		engine.startEngine(customerOrder);
	}
}
