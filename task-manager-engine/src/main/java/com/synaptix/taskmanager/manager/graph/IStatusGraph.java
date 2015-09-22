package com.synaptix.taskmanager.manager.graph;

public interface IStatusGraph {

	public Object getPreviousStatus();

	public Object getCurrentStatus();

	public String getUpdateStatusTaskServiceCode();

}
