package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.example.jpa.model.IBusinessTaskObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAHelper implements IJPAAccess {

	private static JPAHelper instance;

	private EntityManagerFactory emf;
	private EntityManager em;

	private JPAHelper() {
		emf = Persistence.createEntityManagerFactory("examples");
		em = emf.createEntityManager();
	}

	public static synchronized JPAHelper getInstance() {
		if (instance == null) {
			instance = new JPAHelper();
		}

		return instance;
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public <E extends IBusinessTaskObject> E find(Class<E> businessTaskObject, Long id) {
		return getEntityManager().find(businessTaskObject,id);
	}
}
