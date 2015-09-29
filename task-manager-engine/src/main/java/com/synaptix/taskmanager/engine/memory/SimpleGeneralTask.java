package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.IGeneralTask;
import com.synaptix.taskmanager.engine.taskdefinition.IGeneralTaskDefinition;
import com.synaptix.taskmanager.model.ITaskObject;

import java.util.ArrayList;
import java.util.List;

public class SimpleGeneralTask extends AbstractSimpleCommonTask implements IGeneralTask  {

	private final List<ICommonTask> otherBranchFirstTasks;

	private final Class<? extends ITaskObject> taskObjectClass;

	private final Object currentStatus;

	public SimpleGeneralTask(IGeneralTaskDefinition generalTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
		super(generalTaskDefinition);

		this.taskObjectClass = taskObjectClass;
		this.currentStatus = currentStatus;

		this.otherBranchFirstTasks = new ArrayList<ICommonTask>();
	}

	@Override
	public Class<? extends ITaskObject> getTaskObjectClass() {
		return taskObjectClass;
	}

	@Override
	public Object getCurrentStatus() {
		return currentStatus;
	}

	public final List<ICommonTask> getOtherBranchFirstTasks() {
		return otherBranchFirstTasks;
	}

	@Override
	public String toString() {
		return "SimpleGeneralTask -> " + getCurrentStatus();
	}
}
