package com.synaptix.taskmanager.example.jpa.task;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.jpa.JPAHelper;
import com.synaptix.taskmanager.example.jpa.model.Task;
import com.synaptix.taskmanager.example.jpa.model.Todo;

public class SetSummaryTaskService extends AbstractTaskService {

	private final String summary;

	public SetSummaryTaskService(String summary) {
		super();

		this.summary = summary;
	}

	@Override
	public IExecutionResult execute(IEngineContext context,ICommonTask commonTask) {
		Task task = (Task)commonTask;

		Todo todo = JPAHelper.getInstance().findById(Todo.class,task.getBusinessTaskObjectId());

		JPAHelper.getInstance().getEntityManager().getTransaction().begin();

		todo.setSummary(summary);
		JPAHelper.getInstance().getEntityManager().persist(todo);

		JPAHelper.getInstance().getEntityManager().getTransaction().commit();

		return ExecutionResultBuilder.newBuilder().finished();
	}
}
