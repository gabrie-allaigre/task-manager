package com.synaptix.taskmanager.example;

import com.synaptix.taskmanager.engine.MemoryTaskManagerReaderWriter;
import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.DefaultTaskManagerConfiguration;
import com.synaptix.taskmanager.engine.configuration.DefaultTaskManagerConfiguration.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.DefaultTaskDefinitionRegistry;
import com.synaptix.taskmanager.example.tasks.enrichment.INCTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.CANTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.CLOTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.TCOTaskService;
import com.synaptix.taskmanager.manager.taskdefinition.TaskDefinitionBuilder;

public class MainExample {

	public static void main(String[] args) {
		StatusGraphsBuilder<CustomerOrderStatus> builder = StatusGraphsBuilder.newBuilder(CustomerOrderStatus.TCO, "TCO").addNextStatusGraph(CustomerOrderStatus.CLO, "CLO")
				.addNextStatusGraph(CustomerOrderStatus.CAN, "CAN");

		ComponentTaskObjectManagerRegistry taskObjectManagerRegistry = new ComponentTaskObjectManagerRegistry();
		taskObjectManagerRegistry.addTaskObjectManager(new CustomerOrderObjectTypeTaskFactory());

		DefaultTaskDefinitionRegistry taskDefinitionRegistry = new DefaultTaskDefinitionRegistry();
		taskDefinitionRegistry.addTaskDefinition(new TaskDefinitionBuilder("TCO", new TCOTaskService()).build());
		taskDefinitionRegistry.addTaskDefinition(new TaskDefinitionBuilder("CLO", new CLOTaskService()).build());
		taskDefinitionRegistry.addTaskDefinition(new TaskDefinitionBuilder("CAN", new CANTaskService()).build());
		taskDefinitionRegistry.addTaskDefinition(new TaskDefinitionBuilder("INC", new INCTaskService()).build());

		MemoryTaskManagerReaderWriter memoryTaskReaderWriter = new MemoryTaskManagerReaderWriter();

		TaskManagerEngine engine = new TaskManagerEngine(
				new DefaultTaskManagerConfiguration.Builder().addStatusGraphs(ICustomerOrder.class, builder.build()).taskObjectManagerRegistry(taskObjectManagerRegistry)
						.taskServiceRegistry(taskDefinitionRegistry).taskManagerReader(memoryTaskReaderWriter).taskManagerWriter(memoryTaskReaderWriter).build());

		ICustomerOrder customerOrder = new CustomerOrderBuilder().customerOrderNo("123456").build();
		engine.startEngine(customerOrder);

		engine.startEngine(customerOrder);
	}
}
