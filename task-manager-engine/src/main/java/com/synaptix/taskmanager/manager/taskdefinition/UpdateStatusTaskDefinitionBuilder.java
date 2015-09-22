package com.synaptix.taskmanager.manager.taskdefinition;

import com.synaptix.taskmanager.manager.taskservice.ITaskService;

public class UpdateStatusTaskDefinitionBuilder {

	private MyTaskDefinition taskDefinition;

	protected UpdateStatusTaskDefinitionBuilder(String code, ITaskService taskService) {
		super();

		this.taskDefinition = new MyTaskDefinition(code, taskService);
	}

	public IUpdateStatusTaskDefinition build() {
		return taskDefinition;
	}

	public static UpdateStatusTaskDefinitionBuilder newBuilder(String code, ITaskService taskService) {
		return new UpdateStatusTaskDefinitionBuilder(code, taskService);
	}

	private static class MyTaskDefinition implements IUpdateStatusTaskDefinition {

		final String code;

		final ITaskService taskService;

		public MyTaskDefinition(String code, ITaskService taskService) {
			super();

			this.code = code;
			this.taskService = taskService;
		}

		@Override
		public String getCode() {
			return this.code;
		}

		@Override
		public ITaskService getTaskService() {
			return this.taskService;
		}
	}
}
