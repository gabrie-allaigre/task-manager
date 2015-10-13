package com.synaptix.taskmanager.jpa.test.data;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.jpa.model.Task;

public class VerifyCodeTaskService extends AbstractTaskService {

    private final String code;

    public VerifyCodeTaskService(String code) {
        super();

        this.code = code;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        Task task = (Task) commonTask;

        BusinessObject businessObject = JPAHelper.getInstance().getJpaAccess().getEntityManager().find(BusinessObject.class, task.getBusinessTaskObjectId());

        if (code != null && code.equals(businessObject.getCode())) {
            return ExecutionResultBuilder.newBuilder().noChanges().finished();
        }
        return ExecutionResultBuilder.newBuilder().notFinished();
    }

}
