package com.synaptix.taskmanager.simple;

import com.synaptix.taskmanager.model.ITaskCluster;

public class SimpleTaskCluster implements ITaskCluster {

	private boolean checkGraphCreated;

	@Override
	public boolean isCheckGraphCreated() {
		return this.checkGraphCreated;
	}

	@Override
	public void setCheckGraphCreated(boolean checkGraphCreated) {
		this.checkGraphCreated = checkGraphCreated;
	}
}
