package com.synaptix.taskmanager.example.tap.task.fiche;

import com.synaptix.taskmanager.engine.memory.SimpleStatusTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.tap.model.FicheContact;
import com.synaptix.taskmanager.example.tap.model.FicheContactStatus;
import com.synaptix.taskmanager.example.tap.model.Operation;
import com.synaptix.taskmanager.example.tap.model.OperationStatus;

import java.util.List;

public class AbstractFicheStatusTaskService extends AbstractTaskService {

    private final FicheContactStatus ficheContactStatus;

    protected AbstractFicheStatusTaskService(FicheContactStatus ficheContactStatus) {
        super();

        this.ficheContactStatus = ficheContactStatus;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        SimpleStatusTask task = (SimpleStatusTask) commonTask;


        FicheContact ficheContact = task.<FicheContact>getTaskObject();

        List<Operation> operations = ficheContact.getOperations();
        if (operations != null && !operations.isEmpty()) {
            long nb = operations.stream().filter(operation -> ficheContactStatus.equals(operation.getEndFicheContactStatus()) && !OperationStatus.DONE.equals(operation.getOperationStatus()))
                    .count();
            if (nb > 0) {
                return ExecutionResultBuilder.newBuilder().notFinished();
            }
        }

        ficheContact.setFicheContactStatus(ficheContactStatus);

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
