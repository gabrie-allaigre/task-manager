package com.talanlabs.taskmanager.jpa.test.data;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.talanlabs.taskmanager.jpa.JPATask;

public class VerifyCodeTaskService extends AbstractTaskService {

    private final String code;

    public VerifyCodeTaskService(String code) {
        super();

        this.code = code;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        JPATask task = (JPATask) commonTask;

        BusinessObject businessObject = JPAHelper.getInstance().getJpaAccess().getEntityManager().find(BusinessObject.class, task.getTask().getBusinessTaskObjectId());

        if (code != null && code.equals(businessObject.getCode())) {
            return ExecutionResultBuilder.newBuilder().noChanges().finished();
        }
        return ExecutionResultBuilder.newBuilder().notFinished();
    }

}
