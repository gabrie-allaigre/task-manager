package com.synaptix.taskmanager.jpa;

import com.synaptix.taskmanager.jpa.model.IBusinessTaskObject;

import javax.persistence.EntityManager;

public interface IJPAAccess {

    EntityManager getEntityManager();

    <E extends IBusinessTaskObject> E find(Class<E> businessTaskObject, Long id);

    <E extends IBusinessTaskObject> Class<E> instanceToClass(E businessTask);
}
