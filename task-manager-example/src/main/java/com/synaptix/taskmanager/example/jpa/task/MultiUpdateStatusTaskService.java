package com.synaptix.taskmanager.example.jpa.task;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.jpa.JPAHelper;
import com.synaptix.taskmanager.example.jpa.model.Todo;
import com.synaptix.taskmanager.jpa.JPATask;
import com.synaptix.taskmanager.jpa.model.Task;

public class MultiUpdateStatusTaskService extends AbstractTaskService {

    private final String status;

    public MultiUpdateStatusTaskService(String status) {
        super();

        this.status = status;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        Task task = ((JPATask) commonTask).getTask();

        Todo todo = JPAHelper.getInstance().getJpaAccess().getEntityManager().find(Todo.class, task.getBusinessTaskObjectId());

        JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

        todo.setStatus(status);
        JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(todo);

        JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
