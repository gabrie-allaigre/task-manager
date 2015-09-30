package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.jpa.DefaultJPAAccess;
import com.synaptix.taskmanager.jpa.IJPAAccess;

public class JPAHelper {

	private static JPAHelper instance;

	private DefaultJPAAccess jpaAccess;

	private JPAHelper() {
		jpaAccess = new DefaultJPAAccess("examples");
	}

	public static synchronized JPAHelper getInstance() {
		if (instance == null) {
			instance = new JPAHelper();
		}

		return instance;
	}

	public IJPAAccess getJpaAccess() {
		return jpaAccess;
	}
}
