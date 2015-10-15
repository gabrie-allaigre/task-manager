package com.synaptix.taskmanager.example.tap.task.fiche;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.tap.ITapTaskDefinition;
import com.synaptix.taskmanager.example.tap.TapHelper;
import com.synaptix.taskmanager.example.tap.model.FicheContact;
import com.synaptix.taskmanager.example.tap.model.Operation;
import com.synaptix.taskmanager.jpa.JPATask;
import com.synaptix.taskmanager.jpa.model.Task;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class CreateOperationTaskService extends AbstractTaskService {

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        Task task = ((JPATask) commonTask).getTask();

        ITapTaskDefinition tapTaskDefinition = (ITapTaskDefinition)context.getTaskDefinition();

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        FicheContact ficheContact = em.find(FicheContact.class, task.getBusinessTaskObjectId());

        em.getTransaction().begin();

        Operation operation = new Operation();
        operation.setFicheContact(ficheContact);
        operation.setEndFicheContactStatus(tapTaskDefinition.getEndFicheContactStatus());
        operation.setType(tapTaskDefinition.getType());

        em.persist(operation);

        List<Operation> operations = ficheContact.getOperations();
        if (operations == null) {
            operations = new ArrayList<>();
            ficheContact.setOperations(operations);
        }
        operations.add(operation);

        em.persist(ficheContact);

        em.getTransaction().commit();

        context.addTaskObjectsToTaskCluster(operation);

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
