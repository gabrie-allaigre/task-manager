package com.talanlabs.taskmanager.jpa;

import com.talanlabs.taskmanager.model.ITaskObject;

public class StringCurrentStatusTransform implements ICurrentStatusTransform {

    public static final ICurrentStatusTransform INSTANCE = new StringCurrentStatusTransform();

    @Override
    public String toString(Class<? extends ITaskObject> taskObjectClass, Object currentStatus) {
        return (String) currentStatus;
    }

    @Override
    public Object toObject(Class<? extends ITaskObject> taskObjectClass, String currentStatusString) {
        return currentStatusString;
    }
}
