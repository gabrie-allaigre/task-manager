package com.synaptix.taskmanager.model;

public interface ITaskCluster {

    /**
     * If Graph contains tasks with taskObject
     *
     * @return
     */
    boolean isCheckGraphCreated();

    /**
     * If archived cluster, is finish
     *
     * @return
     */
    boolean isCheckArchived();

}
