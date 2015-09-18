package com.talanlabs.taskmanager.jpa;

import com.talanlabs.taskmanager.model.ITaskObject;

public interface ICurrentStatusTransform {

    /**
     * Convert status object to String
     *
     * @param taskObjectClass task object
     * @param currentStatus   status
     * @return string
     */
    String toString(Class<? extends ITaskObject> taskObjectClass, Object currentStatus);

    /**
     * Convert status string to Object
     *
     * @param taskObjectClass     task object
     * @param currentStatusString status
     * @return object
     */
    Object toObject(Class<? extends ITaskObject> taskObjectClass, String currentStatusString);

}
