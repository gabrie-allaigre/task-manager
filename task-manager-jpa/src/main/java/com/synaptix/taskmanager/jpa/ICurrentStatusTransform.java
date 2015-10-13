package com.synaptix.taskmanager.jpa;

public interface ICurrentStatusTransform<E> {

    /**
     * Convert status object to String
     *
     * @param currentStatus status
     * @return string
     */
    String toString(E currentStatus);

    /**
     * Convert status string to Object
     *
     * @param currentStatusString status
     * @return object
     */
    E toObject(String currentStatusString);
}
