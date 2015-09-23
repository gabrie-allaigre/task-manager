package com.synaptix.taskmanager.engine.taskdefinition;

import com.synaptix.taskmanager.engine.taskservice.ITaskService;

public class NormalTaskDefinitionBuilder {

	private TaskDefinitionImpl taskDefinition;

	protected NormalTaskDefinitionBuilder(String code, ITaskService taskService) {
		super();

		this.taskDefinition = new TaskDefinitionImpl(code, taskService);
	}

	public INormalTaskDefinition build() {
		return taskDefinition;
	}

	public static NormalTaskDefinitionBuilder newBuilder(String code, ITaskService taskService) {
		return new NormalTaskDefinitionBuilder(code, taskService);
	}

	private static class TaskDefinitionImpl implements INormalTaskDefinition {

		final String code;

		final ITaskService taskService;

		public TaskDefinitionImpl(String code, ITaskService taskService) {
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
