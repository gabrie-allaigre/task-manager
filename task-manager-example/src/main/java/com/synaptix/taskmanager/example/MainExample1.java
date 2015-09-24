package com.synaptix.taskmanager.example;

import java.util.List;
import java.util.UUID;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.Proxy;
import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.graph.IStatusGraphRegistry;
import com.synaptix.taskmanager.engine.configuration.graph.StatusGraphRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.listener.LogTaskCycleListener;
import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.NormalTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.UpdateStatusTaskDefinitionBuilder;
import com.synaptix.taskmanager.example.business.CustomerOrderBuilder;
import com.synaptix.taskmanager.example.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.business.ICustomerOrder;
import com.synaptix.taskmanager.example.tasks.enrichment.DateClosedTaskService;
import com.synaptix.taskmanager.example.tasks.enrichment.NotConfirmedTaskService;
import com.synaptix.taskmanager.example.tasks.enrichment.ReferenceTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.CANTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.CLOTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.TCOTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.VALTaskService;
import com.synaptix.taskmanager.model.ITaskObject;

public class MainExample1 {

	public static void main(String[] args) {
		List<IStatusGraph<CustomerOrderStatus>> statusGraphs = StatusGraphsBuilder.<CustomerOrderStatus> newBuilder()
				.addNextStatusGraph(CustomerOrderStatus.TCO, "TCO",
						StatusGraphsBuilder.<CustomerOrderStatus> newBuilder()
								.addNextStatusGraph(CustomerOrderStatus.VAL, "VAL",
										StatusGraphsBuilder.<CustomerOrderStatus> newBuilder().addNextStatusGraph(CustomerOrderStatus.TCO, "TCO").addNextStatusGraph(CustomerOrderStatus.CLO, "CLO"))
								.addNextStatusGraph(CustomerOrderStatus.CAN, "CAN"))
				.addNextStatusGraph(CustomerOrderStatus.CAN, "CAN").build();

		IStatusGraphRegistry statusGraphRegistry = StatusGraphRegistryBuilder.newBuilder().addStatusGraphs(ICustomerOrder.class, statusGraphs).build();

		ITaskObjectManager<ICustomerOrder> customerOrderTaskObjectManager = TaskObjectManagerBuilder.newBuilder(ICustomerOrder.class).addTaskChainCriteria(null, CustomerOrderStatus.TCO, "REF")
				.addTaskChainCriteria(CustomerOrderStatus.VAL, CustomerOrderStatus.CLO, "REF2=>DATE").addTaskChainCriteria(CustomerOrderStatus.VAL, CustomerOrderStatus.TCO, "NOT-VAL").build();

		ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder(new TaskObjectManagerRegistryBuilder.IGetClass() {
			@SuppressWarnings("unchecked")
			@Override
			public <F extends ITaskObject<?>> Class<F> getClass(F taskObject) {
				if (taskObject instanceof IComponent) {
					return (Class<F>) ((Proxy) taskObject).straightGetComponentClass();
				}
				return (Class<F>) taskObject.getClass();
			}
		}).addTaskObjectManager(customerOrderTaskObjectManager).build();

		ITaskDefinitionRegistry taskDefinitionRegistry = TaskDefinitionRegistryBuilder.newBuilder()
				.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("TCO", new TCOTaskService()).build())
				.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("VAL", new VALTaskService()).build())
				.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("CLO", new CLOTaskService()).build())
				.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("CAN", new CANTaskService()).build())
				.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("DATE", new DateClosedTaskService()).build())
				.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("REF", new ReferenceTaskService("Ma ref 1")).build())
				.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("REF2", new ReferenceTaskService("Ma ref 2")).build())
				.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("NOT-VAL", new NotConfirmedTaskService()).build()).build();

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().statusGraphRegistry(statusGraphRegistry).taskObjectManagerRegistry(taskObjectManagerRegistry)
				.taskDefinitionRegistry(taskDefinitionRegistry).build());

		engine.addTaskManagerListener(new LogTaskCycleListener());

		ICustomerOrder customerOrder = new CustomerOrderBuilder().id(UUID.randomUUID()).version(0).customerOrderNo("123456").confirmed(false).build();
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
