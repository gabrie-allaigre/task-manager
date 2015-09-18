package com.talanlabs.taskmanager.example.tap.task.operation;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.talanlabs.taskmanager.example.tap.TapHelper;
import com.talanlabs.taskmanager.example.tap.model.Operation;
import com.talanlabs.taskmanager.example.tap.model.OperationStatus;
import com.talanlabs.taskmanager.jpa.JPATask;
import com.talanlabs.taskmanager.jpa.model.Task;

import javax.persistence.EntityManager;

public class AbstractOperationStatusTaskService extends AbstractTaskService {

    private final OperationStatus operationStatus;

    protected AbstractOperationStatusTaskService(OperationStatus operationStatus) {
        super();

        this.operationStatus = operationStatus;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        Task task = ((JPATask) commonTask).getTask();

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        Operation operation = em.find(Operation.class, task.getBusinessTaskObjectId());

        em.getTransaction().begin();

        operation.setOperationStatus(operationStatus);
        em.persist(operation);

        em.getTransaction().commit();

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
