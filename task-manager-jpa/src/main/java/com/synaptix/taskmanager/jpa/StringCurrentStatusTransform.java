package com.synaptix.taskmanager.jpa;

public class StringCurrentStatusTransform implements ICurrentStatusTransform<String> {

    public static final ICurrentStatusTransform<String> INSTANCE = new StringCurrentStatusTransform();

    @Override
    public String toString(String currentStatus) {
        return currentStatus;
    }

    @Override
    public String toObject(String currentStatusString) {
        return currentStatusString;
    }
}
