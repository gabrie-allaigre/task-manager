package com.synaptix.taskmanager.model;

import java.util.List;

public interface ITaskCluster {

	public boolean isCheckGraphCreated();

	public void setCheckGraphCreated(boolean checkGraphCreated);

	public boolean isCheckArchive();

	public void setCheckArchive(boolean checkArchive);

	public List<ITaskClusterDependency> getTaskClusterDependencies();

	public void setTaskClusterDependencies(List<ITaskClusterDependency> taskClusterDependencies);
}
