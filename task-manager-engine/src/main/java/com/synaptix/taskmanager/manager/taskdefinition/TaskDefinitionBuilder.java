package com.synaptix.taskmanager.manager.taskdefinition;

import com.synaptix.taskmanager.manager.taskservice.ITaskService;

public class TaskDefinitionBuilder {

	private MyTaskDefinition taskDefinition;

	public TaskDefinitionBuilder(String code, ITaskService taskService) {
		super();

		this.taskDefinition = new MyTaskDefinition(code, taskService);
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

		int resultDepth;

		public MyTaskDefinition(String code, ITaskService taskService) {
			super();

			this.code = code;
			this.taskService = taskService;

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
		public int getResultDepth() {
			return this.resultDepth;
		}
	}
}
