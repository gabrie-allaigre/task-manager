package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.model.ITaskObject;

import java.util.ArrayList;
import java.util.List;

public class SimpleSubTask extends AbstractSimpleCommonTask  implements ISubTask {

	private final List<ICommonTask> nextTasks;

	private ITaskObject taskObject;

	public SimpleSubTask(String codeTaskDefinition) {
		super(codeTaskDefinition);

		this.nextTasks = new ArrayList<ICommonTask>();
	}

	@Override
	public void setTaskObject(ITaskObject taskObject) {
		this.taskObject = taskObject;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <G extends ITaskObject> G getTaskObject() {
		return (G) taskObject;
	}

	public final List<ICommonTask> getNextTasks() {
		return nextTasks;
	}

	@Override
	public String toString() {
		return "SimpleSubTask -> " + getCodeTaskDefinition();
	}
}
