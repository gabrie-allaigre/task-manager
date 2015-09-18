package com.talanlabs.taskmanager.example.jpa;

import com.talanlabs.taskmanager.jpa.DefaultJPAAccess;

public class JPAHelper {

    private static JPAHelper instance;

    private DefaultJPAAccess jpaAccess;

    private JPAHelper() {
        jpaAccess = new DefaultJPAAccess("jpa");
    }

    public static synchronized JPAHelper getInstance() {
        if (instance == null) {
            instance = new JPAHelper();
        }

        return instance;
    }

    public DefaultJPAAccess getJpaAccess() {
        return jpaAccess;
    }
}
