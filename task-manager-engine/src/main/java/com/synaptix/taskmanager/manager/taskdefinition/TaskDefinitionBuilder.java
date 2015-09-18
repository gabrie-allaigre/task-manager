package com.synaptix.taskmanager.manager.taskdefinition;

import org.joda.time.Duration;

import com.synaptix.taskmanager.manager.taskservice.ITaskService;

public class TaskDefinitionBuilder {

	private MyTaskDefinition taskDefinition;

	public TaskDefinitionBuilder(String code, ITaskService taskService) {
		super();

		this.taskDefinition = new MyTaskDefinition(code, taskService);
	}

	public TaskDefinitionBuilder checkSkippable(boolean checkSkippable) {
		this.taskDefinition.checkSkippable = checkSkippable;
		return this;
	}

	public TaskDefinitionBuilder checkSkippable(String executantRole) {
		this.taskDefinition.executantRole = executantRole;
		return this;
	}

	public TaskDefinitionBuilder managerRole(String managerRole) {
		this.taskDefinition.managerRole = managerRole;
		return this;
	}

	public TaskDefinitionBuilder todoManagerDuration(Duration todoManagerDuration) {
		this.taskDefinition.todoManagerDuration = todoManagerDuration;
		return this;
	}

	public TaskDefinitionBuilder resultDepth(int resultDepth) {
		this.taskDefinition.resultDepth = resultDepth;
		return this;
	}

	public ITaskDefinition build() {
		return taskDefinition;
	}

	private static class MyTaskDefinition implements ITaskDefinition {

		final String code;

		final ITaskService taskService;

		boolean checkSkippable;

		String executantRole;

		String managerRole;

		Duration todoManagerDuration;

		int resultDepth;

		public MyTaskDefinition(String code, ITaskService taskService) {
			super();

			this.code = code;
			this.taskService = taskService;

			this.checkSkippable = true;
			this.executantRole = null;
			this.managerRole = null;
			this.todoManagerDuration = null;
			this.resultDepth = -1;
		}

		@Override
		public String getCode() {
			return this.code;
		}

		@Override
		public ITaskService getTaskService() {
			return this.taskService;
		}

		@Override
		public boolean isCheckSkippable() {
			return this.checkSkippable;
		}

		@Override
		public String getExecutantRole() {
			return this.executantRole;
		}

		@Override
		public String getManagerRole() {
			return this.managerRole;
		}

		@Override
		public Duration getTodoManagerDuration() {
			return this.todoManagerDuration;
		}

		@Override
		public int getResultDepth() {
			return this.resultDepth;
		}
	}
}
