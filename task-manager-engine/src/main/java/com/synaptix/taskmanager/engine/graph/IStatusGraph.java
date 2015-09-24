package com.synaptix.taskmanager.engine.graph;

public interface IStatusGraph<E extends Object> {

	public E getPreviousStatus();

	public E getCurrentStatus();

	public String getUpdateStatusTaskServiceCode();

}
