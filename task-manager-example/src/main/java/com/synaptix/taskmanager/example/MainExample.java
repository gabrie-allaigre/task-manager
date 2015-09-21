package com.synaptix.taskmanager.example;

import java.util.UUID;

import com.synaptix.taskmanager.engine.MemoryTaskManagerReaderWriter;
import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.DefaultTaskManagerConfiguration;
import com.synaptix.taskmanager.engine.configuration.DefaultTaskManagerConfiguration.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.DefaultTaskDefinitionRegistry;
import com.synaptix.taskmanager.example.tasks.enrichment.DateClosedTaskService;
import com.synaptix.taskmanager.example.tasks.enrichment.ReferenceTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.CANTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.CLOTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.TCOTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.VALTaskService;
import com.synaptix.taskmanager.manager.taskdefinition.TaskDefinitionBuilder;

public class MainExample {

	public static void main(String[] args) {
		StatusGraphsBuilder<CustomerOrderStatus> builder = StatusGraphsBuilder.newBuilder(CustomerOrderStatus.TCO, "TCO")
				.addNextStatusGraphsBuilder(StatusGraphsBuilder.newBuilder(CustomerOrderStatus.VAL, "VAL").addNextStatusGraph(CustomerOrderStatus.CLO, "CLO"))
				.addNextStatusGraph(CustomerOrderStatus.CAN, "CAN");

		ComponentTaskObjectManagerRegistry taskObjectManagerRegistry = new ComponentTaskObjectManagerRegistry();
		taskObjectManagerRegistry.addTaskObjectManager(new CustomerOrderObjectTypeTaskFactory());

		DefaultTaskDefinitionRegistry taskDefinitionRegistry = new DefaultTaskDefinitionRegistry();
		taskDefinitionRegistry.addTaskDefinition(new TaskDefinitionBuilder("TCO", new TCOTaskService()).build());
		taskDefinitionRegistry.addTaskDefinition(new TaskDefinitionBuilder("VAL", new VALTaskService()).build());
		taskDefinitionRegistry.addTaskDefinition(new TaskDefinitionBuilder("CLO", new CLOTaskService()).build());
		taskDefinitionRegistry.addTaskDefinition(new TaskDefinitionBuilder("CAN", new CANTaskService()).build());
		taskDefinitionRegistry.addTaskDefinition(new TaskDefinitionBuilder("DATE", new DateClosedTaskService()).build());
		taskDefinitionRegistry.addTaskDefinition(new TaskDefinitionBuilder("REF", new ReferenceTaskService()).build());

		MemoryTaskManagerReaderWriter memoryTaskReaderWriter = new MemoryTaskManagerReaderWriter();

		TaskManagerEngine engine = new TaskManagerEngine(
				new DefaultTaskManagerConfiguration.Builder().addStatusGraphs(ICustomerOrder.class, builder.build()).taskObjectManagerRegistry(taskObjectManagerRegistry)
						.taskServiceRegistry(taskDefinitionRegistry).taskManagerReader(memoryTaskReaderWriter).taskManagerWriter(memoryTaskReaderWriter).build());

		ICustomerOrder customerOrder = new CustomerOrderBuilder().id(UUID.randomUUID()).version(0).customerOrderNo("123456").confirmed(false).build();
		engine.startEngine(customerOrder);

		System.out.println(customerOrder);
		customerOrder.setConfirmed(true);

		engine.startEngine(customerOrder);

		System.out.println(customerOrder);
	}
}
