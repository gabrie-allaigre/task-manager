package com.synaptix.taskmanager.simple;

import java.util.List;

import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskClusterDependency;

public class SimpleTaskCluster implements ITaskCluster {

	private boolean checkGraphCreated;

	private boolean checkArchive;

	@Override
	public boolean isCheckGraphCreated() {
		return this.checkGraphCreated;
	}

	@Override
	public void setCheckGraphCreated(boolean checkGraphCreated) {
		this.checkGraphCreated = checkGraphCreated;
	}

	@Override
	public boolean isCheckArchive() {
		return this.checkArchive;
	}

	@Override
	public void setCheckArchive(boolean checkArchive) {
		this.checkArchive = checkArchive;
	}

	@Override
	public List<ITaskClusterDependency> getTaskClusterDependencies() {
		return null;
	}

	@Override
	public void setTaskClusterDependencies(List<ITaskClusterDependency> taskClusterDependencies) {
	}
}
