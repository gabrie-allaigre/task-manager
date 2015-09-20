package com.synaptix.taskmanager.model;

public interface ITaskCluster {

	/**
	 * If Graph contains tasks with taskObject
	 * 
	 * @return
	 */
	public boolean isCheckGraphCreated();

	/**
	 * If archived cluster, is finish
	 * 
	 * @return
	 */
	public boolean isCheckArchived();

}
