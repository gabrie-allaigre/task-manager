package com.synaptix.taskmanager.engine.taskdefinition;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.synaptix.taskmanager.engine.taskservice.ITaskService;

public class SubTaskDefinitionBuilder {

	private TaskDefinitionImpl taskDefinition;

	private SubTaskDefinitionBuilder(String code, ITaskService taskService) {
		super();

		this.taskDefinition = new TaskDefinitionImpl(code, taskService);
	}

	public ISubTaskDefinition build() {
		return taskDefinition;
	}

	public static SubTaskDefinitionBuilder newBuilder(String code, ITaskService taskService) {
		return new SubTaskDefinitionBuilder(code, taskService);
	}

	private static class TaskDefinitionImpl implements ISubTaskDefinition {

		private final String code;

		private final ITaskService taskService;

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

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
