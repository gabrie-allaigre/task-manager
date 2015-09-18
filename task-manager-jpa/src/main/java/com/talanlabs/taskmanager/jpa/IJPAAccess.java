package com.talanlabs.taskmanager.jpa;

import com.talanlabs.taskmanager.jpa.model.IBusinessTaskObject;

import javax.persistence.EntityManager;

public interface IJPAAccess {

    EntityManager getEntityManager();

    <E extends IBusinessTaskObject> E find(Class<E> businessTaskObject, Long id);

    <E extends IBusinessTaskObject> Class<E> instanceToClass(E businessTask);
}
