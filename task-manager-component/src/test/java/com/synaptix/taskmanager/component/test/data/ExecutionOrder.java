package com.synaptix.taskmanager.component.test.data;

import com.synaptix.taskmanager.model.ITaskObject;

public class ExecutionOrder implements ITaskObject {

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
