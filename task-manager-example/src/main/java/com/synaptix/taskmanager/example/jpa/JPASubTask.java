package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.engine.taskdefinition.ISubTaskDefinition;

public class JPASubTask extends AbstractJPACommonTask implements ISubTask {

	public JPASubTask(ISubTaskDefinition taskDefinition) {
		super(taskDefinition);
	}

}
