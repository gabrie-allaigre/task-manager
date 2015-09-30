package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.example.jpa.model.IBusinessTaskObject;

import javax.persistence.EntityManager;

public interface IJPAAccess {

	EntityManager getEntityManager();

	<E extends IBusinessTaskObject> E find(Class<E> businessTaskObject,Long id);
}
