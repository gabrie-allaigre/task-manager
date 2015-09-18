package com.synaptix.taskmanager.model;

public interface IStatusGraph<E extends Enum<E>> {

	public E getCurrentStatus();

	public E getNextStatus();

	public String getCodeTaskType();

}
