package com.talanlabs.taskmanager.component.test.data;

import com.talanlabs.taskmanager.model.ITaskObject;

public class ExecutionOrder implements ITaskObject {

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
