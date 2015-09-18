package com.talanlabs.taskmanager.example.tap.task.operation;

import com.talanlabs.taskmanager.example.tap.model.OperationStatus;

public class DoneStatusTaskService extends AbstractOperationStatusTaskService {

    public DoneStatusTaskService() {
        super(OperationStatus.DONE);
    }

}
