package com.synaptix.taskmanager.example.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAHelper {

	private static JPAHelper instance;

	private EntityManagerFactory emf;
	private EntityManager em;

	private JPAHelper() {
		emf = Persistence.createEntityManagerFactory("todos");
		em = emf.createEntityManager();
	}

	public static synchronized JPAHelper getInstance() {
		if (instance == null) {
			instance = new JPAHelper();
		}

		return instance;
	}

	public EntityManager getEntityManager() {
		return em;
	}
}
