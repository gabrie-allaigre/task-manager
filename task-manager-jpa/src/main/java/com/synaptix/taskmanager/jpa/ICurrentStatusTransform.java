package com.synaptix.taskmanager.jpa;

public interface ICurrentStatusTransform {

    /**
     * Convert status object to String
     *
     * @param currentStatus status
     * @return string
     */
    String toString(Object currentStatus);

    /**
     * Convert status string to Object
     *
     * @param currentStatusString status
     * @return object
     */
    Object toObject(String currentStatusString);

}
