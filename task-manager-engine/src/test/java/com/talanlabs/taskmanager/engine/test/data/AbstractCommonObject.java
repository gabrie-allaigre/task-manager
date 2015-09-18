package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.model.ITaskObject;

public abstract class AbstractCommonObject implements ITaskObject {

    private String status;

    private String code;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
