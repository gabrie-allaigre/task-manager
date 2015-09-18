package com.talanlabs.taskmanager.example.tap;

import com.talanlabs.taskmanager.jpa.DefaultJPAAccess;

public class TapHelper {

    private static TapHelper instance;

    private DefaultJPAAccess jpaAccess;

    private TapHelper() {
        jpaAccess = new DefaultJPAAccess("tap");
    }

    public static synchronized TapHelper getInstance() {
        if (instance == null) {
            instance = new TapHelper();
        }

        return instance;
    }

    public DefaultJPAAccess getJpaAccess() {
        return jpaAccess;
    }
}
