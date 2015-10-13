package com.synaptix.taskmanager.example.tap.task.fiche;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.tap.TapHelper;
import com.synaptix.taskmanager.example.tap.model.FicheContact;
import com.synaptix.taskmanager.example.tap.model.FicheContactStatus;
import com.synaptix.taskmanager.example.tap.model.Operation;
import com.synaptix.taskmanager.example.tap.model.OperationStatus;
import com.synaptix.taskmanager.jpa.model.Task;

import javax.persistence.EntityManager;
import java.util.List;

public class CommandeStatusTaskService extends AbstractTaskService {

    public CommandeStatusTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        Task task = (Task) commonTask;

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        FicheContact ficheContact = em.find(FicheContact.class, task.getBusinessTaskObjectId());

        List<Operation> operations = ficheContact.getOperations();
        if (operations != null && !operations.isEmpty()) {
            long nb = operations.stream().filter(operation -> FicheContactStatus.COMMANDE.equals(operation.getEndFicheContactStatus()) && !OperationStatus.DONE.equals(operation.getOperationStatus()))
                    .count();
            if (nb > 0) {
                return ExecutionResultBuilder.newBuilder().notFinished();
            }
        }

        em.getTransaction().begin();

        ficheContact.setFicheContactStatus(FicheContactStatus.COMMANDE);
        em.persist(ficheContact);

        em.getTransaction().commit();

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
