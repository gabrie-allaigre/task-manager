package com.synaptix.taskmanager.engine.graph;

public interface IStatusGraph<E> {

    E getPreviousStatus();

    E getCurrentStatus();

    String getStatusTaskServiceCode();

}
