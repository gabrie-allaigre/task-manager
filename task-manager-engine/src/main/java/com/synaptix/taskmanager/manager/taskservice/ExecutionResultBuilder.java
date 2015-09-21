package com.synaptix.taskmanager.manager.taskservice;

import com.synaptix.component.model.IStackResult;

public class ExecutionResultBuilder {

	private final ExecutionResultImpl executionResultImpl;

	public ExecutionResultBuilder() {
		super();

		this.executionResultImpl = new ExecutionResultImpl();
	}

	public ITaskService.IExecutionResult finished() {
		executionResultImpl.finished = true;
		return executionResultImpl;
	}

	public ITaskService.IExecutionResult notFinished() {
		executionResultImpl.finished = false;
		return executionResultImpl;
	}

	public ExecutionResultBuilder stackResult(IStackResult stackResult) {
		executionResultImpl.stackResult = stackResult;
		return this;
	}

	public ExecutionResultBuilder resultStatus(String resultStatus) {
		executionResultImpl.resultStatus = resultStatus;
		return this;
	}

	/**
	 * Sets the result desc. If null, uses the resultDesc of the first stack
	 */
	public ExecutionResultBuilder resultDesc(String resultDesc) {
		executionResultImpl.resultDesc = resultDesc;
		return this;
	}

	/**
	 * Set to true if object cluster has changed. Task manager will stop at end of task, and restart on new object cluster.
	 */
	public ExecutionResultBuilder mustStopAndRestartTaskManager(boolean b) {
		executionResultImpl.mustStopAndRestartTaskManager = b;
		return this;
	}

	public static final class ExecutionResultImpl implements ITaskService.IExecutionResult {

		private boolean finished;

		private IStackResult stackResult;

		private String resultStatus;

		private String resultDesc;

		private boolean mustStopAndRestartTaskManager;

		public ExecutionResultImpl() {
			super();
		}

		@Override
		public boolean isFinished() {
			return finished;
		}

		@Override
		public IStackResult getStackResult() {
			return stackResult;
		}

		@Override
		public String getResultStatus() {
			return resultStatus;
		}

		@Override
		public String getResultDesc() {
			return resultDesc;
		}

		@Override
		public boolean mustStopAndRestartTaskManager() {
			return mustStopAndRestartTaskManager;
		}
	}
}
