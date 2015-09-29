package com.synaptix.taskmanager.example.jpa.task;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.jpa.JPAHelper;
import com.synaptix.taskmanager.example.jpa.model.Task;
import com.synaptix.taskmanager.example.jpa.model.Todo;

public class MultiUpdateStatusTaskService extends AbstractTaskService {

	private final String status;

	public MultiUpdateStatusTaskService(String status) {
		super();

		this.status = status;
	}

	@Override
	public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
		Task task = (Task)commonTask;

		Todo todo = JPAHelper.getInstance().findById(Todo.class,task.getBusinessTaskObjectId());

		JPAHelper.getInstance().getEntityManager().getTransaction().begin();

		todo.setStatus(status);
		JPAHelper.getInstance().getEntityManager().persist(todo);

		JPAHelper.getInstance().getEntityManager().getTransaction().commit();

		return ExecutionResultBuilder.newBuilder().finished();
	}
}
