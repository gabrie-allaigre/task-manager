package com.synaptix.taskmanager.example.tap.task.operation;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.tap.TapHelper;
import com.synaptix.taskmanager.example.tap.model.Operation;
import com.synaptix.taskmanager.example.tap.model.OperationStatus;
import com.synaptix.taskmanager.jpa.model.Task;

import javax.persistence.EntityManager;

public class DoneStatusTaskService extends AbstractTaskService {

    public DoneStatusTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        Task task = (Task) commonTask;

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        Operation operation = em.find(Operation.class, task.getBusinessTaskObjectId());

        em.getTransaction().begin();

        operation.setOperationStatus(OperationStatus.DONE);
        em.persist(operation);

        em.getTransaction().commit();

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
