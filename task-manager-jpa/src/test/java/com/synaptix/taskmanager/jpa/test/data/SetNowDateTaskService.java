package com.synaptix.taskmanager.jpa.test.data;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.jpa.JPATask;

import java.util.Date;

public class SetNowDateTaskService extends AbstractTaskService {

    public SetNowDateTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        JPATask task = (JPATask) commonTask;

        BusinessObject businessObject = JPAHelper.getInstance().getJpaAccess().getEntityManager().find(BusinessObject.class, task.getTask().getBusinessTaskObjectId());

        JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

        businessObject.setDate(new Date());
        JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(businessObject);

        JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
