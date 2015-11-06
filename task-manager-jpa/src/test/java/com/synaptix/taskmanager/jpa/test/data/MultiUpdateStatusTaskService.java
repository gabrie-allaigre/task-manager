package com.synaptix.taskmanager.jpa.test.data;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.jpa.JPATask;

public class MultiUpdateStatusTaskService extends AbstractTaskService {

    private final String status;

    public MultiUpdateStatusTaskService(String status) {
        super();

        this.status = status;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        JPATask task = (JPATask) commonTask;

        BusinessObject businessObject = JPAHelper.getInstance().getJpaAccess().getEntityManager().find(BusinessObject.class, task.getTask().getBusinessTaskObjectId());

        JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

        businessObject.setStatus(status);
        JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);

        JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
