package com.synaptix.taskmanager.model;

/**
 * 
 * @param <E>
 *            Object statuses enumeration.
 */
public interface ITaskObject<E extends Enum<E>> {

	public E getStatus();

	public void setStatus(E status);

}
