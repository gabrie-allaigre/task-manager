package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MultiParamsTaskService extends AbstractTaskService {

	private final String newCode;

	private final int entier;

	private final float rien;

	private final MyStatus status;

	private final double d;

	public MultiParamsTaskService(String newCode, int entier, float rien, MyStatus status) {
		super();

		this.newCode = newCode;
		this.entier = entier;
		this.rien = rien;
		this.status = status;
		this.d = 0;
	}

	public MultiParamsTaskService(String newCode, int entier, float rien, double d) {
		super();

		this.newCode = newCode;
		this.entier = entier;
		this.rien = rien;
		this.status = null;
		this.d = d;
	}

	public String getNewCode() {
		return newCode;
	}

	public int getEntier() {
		return entier;
	}

	public float getRien() {
		return rien;
	}

	public MyStatus getStatus() {
		return status;
	}

	public double getD() {
		return d;
	}

	@Override
	public IExecutionResult execute(IEngineContext context, ICommonTask task) {
		((SimpleSubTask) task).<BusinessObject>getTaskObject().setCode(newCode);
		return ExecutionResultBuilder.newBuilder().finished();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
