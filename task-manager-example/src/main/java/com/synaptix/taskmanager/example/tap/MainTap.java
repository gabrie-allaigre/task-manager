package com.synaptix.taskmanager.example.tap;

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
import com.synaptix.taskmanager.engine.taskservice.ITaskService;
import com.synaptix.taskmanager.example.tap.model.FicheContact;
import com.synaptix.taskmanager.example.tap.model.FicheContactStatus;
import com.synaptix.taskmanager.example.tap.model.Operation;
import com.synaptix.taskmanager.example.tap.model.OperationStatus;
import com.synaptix.taskmanager.example.tap.task.fiche.*;
import com.synaptix.taskmanager.example.tap.task.operation.CurrentStatusTaskService;
import com.synaptix.taskmanager.example.tap.task.operation.DoneStatusTaskService;
import com.synaptix.taskmanager.example.tap.task.operation.WaitItemTaskService;
import com.synaptix.taskmanager.model.ITaskCluster;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Scanner;

public class MainTap {

    public static void main(String[] args) {
        List<IStatusGraph<FicheContactStatus>> ficheContactStatusGraphs = StatusGraphsBuilder.<FicheContactStatus>newBuilder().addNextStatusGraph(FicheContactStatus.ETUDE, "ETUDE_TASK",
                StatusGraphsBuilder.<FicheContactStatus>newBuilder().addNextStatusGraph(FicheContactStatus.VALIDE, "VALIDE_TASK", StatusGraphsBuilder.<FicheContactStatus>newBuilder()
                        .addNextStatusGraph(FicheContactStatus.COMMANDE, "COMMANDE_TASK",
                                StatusGraphsBuilder.<FicheContactStatus>newBuilder().addNextStatusGraph(FicheContactStatus.TERMINE, "TERMINE_TASK")))).build();
        ITaskObjectManager<FicheContactStatus, FicheContact> ficheContactTaskObjectManager = TaskObjectManagerBuilder.<FicheContactStatus, FicheContact>newBuilder(FicheContact.class)
                .statusGraphs(ficheContactStatusGraphs).addTaskChainCriteria(null, FicheContactStatus.ETUDE, "CREATE_OP1_TASK,CREATE_OP2_TASK,CREATE_OP3_TASK").build();

        List<IStatusGraph<OperationStatus>> operationStatusGraphs = StatusGraphsBuilder.<OperationStatus>newBuilder()
                .addNextStatusGraph(OperationStatus.CURRENT, "CURRENT_TASK", StatusGraphsBuilder.<OperationStatus>newBuilder().addNextStatusGraph(OperationStatus.DONE, "DONE_TASK")).build();
        ITaskObjectManager<OperationStatus, Operation> operationTaskObjectManager = TaskObjectManagerBuilder.<OperationStatus, Operation>newBuilder(Operation.class).statusGraphs(operationStatusGraphs)
                .addTaskChainCriteria(OperationStatus.CURRENT, OperationStatus.DONE, "WAIT_ITEM_TASK").build();

        ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(ficheContactTaskObjectManager)
                .addTaskObjectManager(operationTaskObjectManager).build();

        ITaskService createOperationTaskService = new CreateOperationTaskService();

        ITaskDefinitionRegistry taskDefinitionRegistry = TaskDefinitionRegistryBuilder.newBuilder()
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("ETUDE_TASK", new EtudeStatusTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("VALIDE_TASK", new ValideStatusTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("COMMANDE_TASK", new CommandeStatusTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("TERMINE_TASK", new TermineStatusTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("CURRENT_TASK", new CurrentStatusTaskService()).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("DONE_TASK", new DoneStatusTaskService()).build())
                .addTaskDefinition(TapTaskDefinitionBuilder.newBuilder("CREATE_OP1_TASK", createOperationTaskService).type("accompagnement").endFicheContactStatus(FicheContactStatus.VALIDE).build())
                .addTaskDefinition(TapTaskDefinitionBuilder.newBuilder("CREATE_OP2_TASK", createOperationTaskService).type("reseau").endFicheContactStatus(FicheContactStatus.TERMINE).build())
                .addTaskDefinition(TapTaskDefinitionBuilder.newBuilder("CREATE_OP3_TASK", createOperationTaskService).type("rus").endFicheContactStatus(FicheContactStatus.VALIDE).build())
                .addTaskDefinition(TaskDefinitionBuilder.newBuilder("WAIT_ITEM_TASK", new WaitItemTaskService()).build()).build();

        ITaskManagerConfiguration taskManagerConfiguration = TaskManagerConfigurationBuilder.newBuilder().taskObjectManagerRegistry(taskObjectManagerRegistry)
                .taskDefinitionRegistry(taskDefinitionRegistry).build();
        TaskManagerEngine engine = new TaskManagerEngine(taskManagerConfiguration);

        FicheContact ficheContact = new FicheContact();

        ITaskCluster cluster = engine.startEngine(ficheContact);

        System.out.println("Fiche contact " + ficheContact);

        showOperations(ficheContact);

        Scanner scanner = new Scanner(System.in);

        while (!cluster.isCheckArchived()) {
            System.out.println("Champs");
            String field = scanner.nextLine();
            System.out.println("Valeur");
            String value = scanner.nextLine();

            try {
                BeanUtils.setProperty(ficheContact, field, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            engine.startEngine(ficheContact);

            System.out.println("Fiche contact " + ficheContact);

            showOperations(ficheContact);
        }
    }

    private static void showOperations(FicheContact ficheContact) {
        System.out.println("------ Operations ------");
        List<Operation> operations = ficheContact.getOperations();
        if (operations != null) {
            operations.forEach(System.out::println);
            System.out.println("Size: " + operations.size());
        } else {
            System.out.println("Size: 0");
        }
    }
}
