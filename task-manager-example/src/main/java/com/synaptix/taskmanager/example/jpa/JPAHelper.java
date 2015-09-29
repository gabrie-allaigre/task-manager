package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.example.jpa.model.IBusinessTaskObject;
import com.synaptix.taskmanager.example.jpa.model.IEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class JPAHelper {

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

	public EntityManager getEntityManager() {
		return em;
	}

	public <E extends IEntity> E findById(Class<E> clazz, Long id) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<E> cq = cb.createQuery(clazz);
		Root<E> r = cq.from(clazz);
		cq.where(cb.equal(r.get("id"), id));
		TypedQuery<E> query = getEntityManager().createQuery(cq);
		return query.getSingleResult();
	}
}
