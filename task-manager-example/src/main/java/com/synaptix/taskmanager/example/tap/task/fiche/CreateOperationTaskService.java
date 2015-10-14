package com.synaptix.taskmanager.example.tap.task.fiche;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.tap.ITapTaskDefinition;
import com.synaptix.taskmanager.example.tap.model.FicheContact;
import com.synaptix.taskmanager.example.tap.model.Operation;

import java.util.ArrayList;
import java.util.List;

public class CreateOperationTaskService extends AbstractTaskService {

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        SimpleSubTask task = (SimpleSubTask) commonTask;

        ITapTaskDefinition tapTaskDefinition = (ITapTaskDefinition)context.getTaskDefinition();

        FicheContact ficheContact = task.<FicheContact>getTaskObject();

        Operation operation = new Operation();
        operation.setFicheContact(ficheContact);
        operation.setEndFicheContactStatus(tapTaskDefinition.getEndFicheContactStatus());
        operation.setType(tapTaskDefinition.getType());

        List<Operation> operations = ficheContact.getOperations();
        if (operations == null) {
            operations = new ArrayList<>();
            ficheContact.setOperations(operations);
        }
        operations.add(operation);

        context.addTaskObjectsToTaskCluster(operation);

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
