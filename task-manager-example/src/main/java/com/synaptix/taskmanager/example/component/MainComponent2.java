package com.synaptix.taskmanager.example.component;

import com.synaptix.taskmanager.component.ComponentInstanceToClass;
import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.listener.LogTaskCycleListener;
import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.SubTaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.StatusTaskDefinitionBuilder;
import com.synaptix.taskmanager.example.component.business.CustomerOrderBuilder;
import com.synaptix.taskmanager.example.component.business.CustomerOrderStatus;
import com.synaptix.taskmanager.example.component.business.ICustomerOrder;
import com.synaptix.taskmanager.example.component.task.enrichment.DateClosedTaskService;
import com.synaptix.taskmanager.example.component.task.enrichment.NotConfirmedTaskService;
import com.synaptix.taskmanager.example.component.task.enrichment.ReferenceTaskService;
import com.synaptix.taskmanager.example.component.task.updatestatus.CANTaskService;
import com.synaptix.taskmanager.example.component.task.updatestatus.CLOTaskService;
import com.synaptix.taskmanager.example.component.task.updatestatus.TCOTaskService;
import com.synaptix.taskmanager.example.component.task.updatestatus.VALTaskService;

import java.util.List;
import java.util.UUID;

public class MainComponent2 {

	public static void main(String[] args) {
		List<IStatusGraph<CustomerOrderStatus>> statusGraphs = StatusGraphsBuilder.<CustomerOrderStatus> newBuilder()
				.addNextStatusGraph(CustomerOrderStatus.TCO, "TCO",
						StatusGraphsBuilder.<CustomerOrderStatus> newBuilder()
								.addNextStatusGraph(CustomerOrderStatus.VAL, "VAL",
										StatusGraphsBuilder.<CustomerOrderStatus> newBuilder().addNextStatusGraph(CustomerOrderStatus.TCO, "TCO").addNextStatusGraph(CustomerOrderStatus.CLO, "CLO"))
								.addNextStatusGraph(CustomerOrderStatus.CAN, "CAN"))
				.addNextStatusGraph(CustomerOrderStatus.CAN, "CAN").build();

		ITaskObjectManager<CustomerOrderStatus,ICustomerOrder> customerOrderTaskObjectManager = TaskObjectManagerBuilder.<CustomerOrderStatus,ICustomerOrder>newBuilder(ICustomerOrder.class).statusGraphs(statusGraphs).addTaskChainCriteria(
				null, CustomerOrderStatus.TCO, "REF")
				.addTaskChainCriteria(CustomerOrderStatus.VAL, CustomerOrderStatus.CLO, "REF2=>DATE").addTaskChainCriteria(CustomerOrderStatus.VAL, CustomerOrderStatus.TCO, "NOT-VAL").build();

		ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().instanceToClass(ComponentInstanceToClass.INSTANCE).addTaskObjectManager(
				customerOrderTaskObjectManager).build();

		ITaskDefinitionRegistry taskDefinitionRegistry = TaskDefinitionRegistryBuilder.newBuilder()
				.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("TCO", new TCOTaskService()).build())
				.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("VAL", new VALTaskService()).build())
				.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("CLO", new CLOTaskService()).build())
				.addStatusTaskDefinition(StatusTaskDefinitionBuilder.newBuilder("CAN", new CANTaskService()).build())
				.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("DATE", new DateClosedTaskService()).build())
				.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("REF", new ReferenceTaskService("Ma ref 1")).build())
				.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("REF2", new ReferenceTaskService("Ma ref 2")).build())
				.addSubTaskDefinition(SubTaskDefinitionBuilder.newBuilder("NOT-VAL", new NotConfirmedTaskService()).build()).build();

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(taskObjectManagerRegistry)
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
