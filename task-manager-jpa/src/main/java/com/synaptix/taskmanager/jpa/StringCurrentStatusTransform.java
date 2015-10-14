package com.synaptix.taskmanager.jpa;

public class StringCurrentStatusTransform implements ICurrentStatusTransform {

    public static final ICurrentStatusTransform INSTANCE = new StringCurrentStatusTransform();

    @Override
    public String toString(Object currentStatus) {
        return (String) currentStatus;
    }

    @Override
    public Object toObject(String currentStatusString) {
        return currentStatusString;
    }
}
