package com.synaptix.taskmanager.example;

import java.util.List;
import java.util.UUID;

import com.synaptix.taskmanager.engine.TaskManagerEngine;
import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.graph.DefaultStatusGraphRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.DefaultTaskDefinitionRegistry;
import com.synaptix.taskmanager.example.tasks.enrichment.DateClosedTaskService;
import com.synaptix.taskmanager.example.tasks.enrichment.NotConfirmedTaskService;
import com.synaptix.taskmanager.example.tasks.enrichment.ReferenceTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.CANTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.CLOTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.TCOTaskService;
import com.synaptix.taskmanager.example.tasks.updatestatus.VALTaskService;
import com.synaptix.taskmanager.manager.graph.IStatusGraph;
import com.synaptix.taskmanager.manager.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.manager.taskdefinition.NormalTaskDefinitionBuilder;
import com.synaptix.taskmanager.manager.taskdefinition.UpdateStatusTaskDefinitionBuilder;
import com.synaptix.taskmanager.simple.MemoryTaskManagerReaderWriter;

public class MainExample {

	public static void main(String[] args) {
		List<IStatusGraph> statusGraphs = StatusGraphsBuilder.newBuilder()
				.addNextStatusGraph(CustomerOrderStatus.TCO, "TCO",
						StatusGraphsBuilder.newBuilder()
								.addNextStatusGraph(CustomerOrderStatus.VAL, "VAL",
										StatusGraphsBuilder.newBuilder().addNextStatusGraph(CustomerOrderStatus.TCO, "TCO").addNextStatusGraph(CustomerOrderStatus.CLO, "CLO"))
								.addNextStatusGraph(CustomerOrderStatus.CAN, "CAN"))
				.addNextStatusGraph(CustomerOrderStatus.CAN, "CAN").build();

		DefaultStatusGraphRegistry statusGraphRegistry = new DefaultStatusGraphRegistry();
		statusGraphRegistry.addStatusGraphs(ICustomerOrder.class, statusGraphs);

		ComponentTaskObjectManagerRegistry taskObjectManagerRegistry = new ComponentTaskObjectManagerRegistry();
		taskObjectManagerRegistry.addTaskObjectManager(new CustomerOrderTaskObjectManager());

		DefaultTaskDefinitionRegistry taskDefinitionRegistry = new DefaultTaskDefinitionRegistry();
		taskDefinitionRegistry.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("TCO", new TCOTaskService()).build());
		taskDefinitionRegistry.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("VAL", new VALTaskService()).build());
		taskDefinitionRegistry.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("CLO", new CLOTaskService()).build());
		taskDefinitionRegistry.addUpdateStatusTaskDefinition(UpdateStatusTaskDefinitionBuilder.newBuilder("CAN", new CANTaskService()).build());

		taskDefinitionRegistry.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("DATE", new DateClosedTaskService()).build());
		taskDefinitionRegistry.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("REF", new ReferenceTaskService()).build());
		taskDefinitionRegistry.addNormalTaskDefinition(NormalTaskDefinitionBuilder.newBuilder("NOT-VAL", new NotConfirmedTaskService()).build());

		MemoryTaskManagerReaderWriter memoryTaskReaderWriter = new MemoryTaskManagerReaderWriter();

		TaskManagerEngine engine = new TaskManagerEngine(TaskManagerConfigurationBuilder.newBuilder().statusGraphRegistry(statusGraphRegistry).taskObjectManagerRegistry(taskObjectManagerRegistry)
				.taskServiceRegistry(taskDefinitionRegistry).taskManagerReader(memoryTaskReaderWriter).taskManagerWriter(memoryTaskReaderWriter).build());

		ICustomerOrder customerOrder = new CustomerOrderBuilder().id(UUID.randomUUID()).version(0).customerOrderNo("123456").confirmed(false).build();
		engine.startEngine(customerOrder);

		System.out.println(customerOrder);
		customerOrder.setConfirmed(true);

		engine.startEngine(customerOrder);

		System.out.println(customerOrder);
	}
}
