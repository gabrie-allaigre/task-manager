package com.synaptix.taskmanager.example.jpa.task;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.jpa.JPAHelper;
import com.synaptix.taskmanager.example.jpa.model.Todo;
import com.synaptix.taskmanager.jpa.model.Task;

public class SetNameTaskService extends AbstractTaskService {

    private final String name;

    public SetNameTaskService(String name) {
        super();

        this.name = name;
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        Task task = (Task) commonTask;

        Todo todo = JPAHelper.getInstance().getJpaAccess().getEntityManager().find(Todo.class, task.getBusinessTaskObjectId());

        JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().begin();

        todo.setName(name);
        JPAHelper.getInstance().getJpaAccess().getEntityManager().persist(todo);

        JPAHelper.getInstance().getJpaAccess().getEntityManager().getTransaction().commit();

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
