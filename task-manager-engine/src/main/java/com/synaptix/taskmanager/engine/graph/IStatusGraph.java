package com.synaptix.taskmanager.engine.graph;

public interface IStatusGraph {

	public Object getPreviousStatus();

	public Object getCurrentStatus();

	public String getUpdateStatusTaskServiceCode();

}
