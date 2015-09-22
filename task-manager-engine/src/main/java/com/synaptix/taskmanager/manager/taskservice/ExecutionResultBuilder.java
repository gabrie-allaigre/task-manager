package com.synaptix.taskmanager.manager.taskservice;

public class ExecutionResultBuilder {

	private final ExecutionResultImpl executionResultImpl;

	protected ExecutionResultBuilder() {
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

	public ExecutionResultBuilder result(Object result) {
		executionResultImpl.result = result;
		return this;
	}

	/**
	 * Set to true if object cluster has changed. Task manager will stop at end of task, and restart on new object cluster.
	 */
	public ExecutionResultBuilder mustStopAndRestartTaskManager(boolean b) {
		executionResultImpl.mustStopAndRestartTaskManager = b;
		return this;
	}

	public static ExecutionResultBuilder newBuilder() {
		return new ExecutionResultBuilder();
	}

	private static final class ExecutionResultImpl implements ITaskService.IExecutionResult {

		private boolean finished;

		private Object result;

		private boolean mustStopAndRestartTaskManager;

		public ExecutionResultImpl() {
			super();
		}

		@Override
		public boolean isFinished() {
			return finished;
		}

		@Override
		public Object getResult() {
			return result;
		}

		@Override
		public boolean mustStopAndRestartTaskManager() {
			return mustStopAndRestartTaskManager;
		}
	}
}
