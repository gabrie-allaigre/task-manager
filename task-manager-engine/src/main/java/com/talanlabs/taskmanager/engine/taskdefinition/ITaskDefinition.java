package com.talanlabs.taskmanager.engine.taskdefinition;

import com.talanlabs.taskmanager.engine.taskservice.ITaskService;

public interface ITaskDefinition {

    String getCode();

    ITaskService getTaskService();

}
