package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.engine.manager.TaskObjectManagerBuilder;

public class MyGetStatus implements TaskObjectManagerBuilder.IGetStatus<String, BusinessObject> {

    @Override
    public String getStatus(BusinessObject taskObject) {
        return taskObject.getStatus();
    }
}
