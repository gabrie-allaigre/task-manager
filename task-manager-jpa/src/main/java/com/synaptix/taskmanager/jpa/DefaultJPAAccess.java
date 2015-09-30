package com.synaptix.taskmanager.jpa;

import com.synaptix.taskmanager.jpa.model.IBusinessTaskObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DefaultJPAAccess implements IJPAAccess {

	private final String persistenceUnitName;

	private EntityManagerFactory emf;

	private EntityManager em;

	public DefaultJPAAccess(String persistenceUnitName) {
		super();

		this.persistenceUnitName = persistenceUnitName;
	}

	public void start() {
		emf = Persistence.createEntityManagerFactory(persistenceUnitName);
		em = emf.createEntityManager();
	}

	public void stop() {
		em.close();
		emf.close();
	}

	public final EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}

	@Override
	public final EntityManager getEntityManager() {
		return em;
	}

	@Override
	public <E extends IBusinessTaskObject> E find(Class<E> businessTaskObject, Long id) {
		return getEntityManager().find(businessTaskObject,id);
	}

	@Override
	public <E extends IBusinessTaskObject> Class<E> instanceToClass(E businessTask) {
		return (Class<E>)businessTask.getClass();
	}
}
