package com.synaptix.taskmanager.engine.taskdefinition;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.synaptix.taskmanager.engine.taskservice.ITaskService;

public class TaskDefinitionBuilder {

	private MyTaskDefinition taskDefinition;

	private TaskDefinitionBuilder(String code, ITaskService taskService) {
		super();

		this.taskDefinition = new MyTaskDefinition(code, taskService);
	}

	public ITaskDefinition build() {
		return taskDefinition;
	}

	public static TaskDefinitionBuilder newBuilder(String code, ITaskService taskService) {
		return new TaskDefinitionBuilder(code, taskService);
	}

	private static class MyTaskDefinition implements ITaskDefinition {

		private final String code;

		private final ITaskService taskService;

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

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
