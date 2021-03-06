package com.talanlabs.taskmanager.example.tap.task.fiche;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.talanlabs.taskmanager.example.tap.TapHelper;
import com.talanlabs.taskmanager.example.tap.model.FicheContact;
import com.talanlabs.taskmanager.example.tap.model.FicheContactStatus;
import com.talanlabs.taskmanager.example.tap.model.Operation;
import com.talanlabs.taskmanager.example.tap.model.OperationStatus;
import com.talanlabs.taskmanager.jpa.JPATask;
import com.talanlabs.taskmanager.jpa.model.Task;

import javax.persistence.EntityManager;
import java.util.List;

public class AbstractFicheStatusTaskService extends AbstractTaskService {

    private final FicheContactStatus ficheContactStatus;

    protected AbstractFicheStatusTaskService(FicheContactStatus ficheContactStatus) {
        super();

        this.ficheContactStatus = ficheContactStatus;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        Task task = ((JPATask) commonTask).getTask();

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        FicheContact ficheContact = em.find(FicheContact.class, task.getBusinessTaskObjectId());

        List<Operation> operations = ficheContact.getOperations();
        if (operations != null && !operations.isEmpty()) {
            long nb = operations.stream().filter(operation -> ficheContactStatus.equals(operation.getEndFicheContactStatus()) && !OperationStatus.DONE.equals(operation.getOperationStatus()))
                    .count();
            if (nb > 0) {
                return ExecutionResultBuilder.newBuilder().notFinished();
            }
        }

        em.getTransaction().begin();

        ficheContact.setFicheContactStatus(ficheContactStatus);
        em.persist(ficheContact);

        em.getTransaction().commit();

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
