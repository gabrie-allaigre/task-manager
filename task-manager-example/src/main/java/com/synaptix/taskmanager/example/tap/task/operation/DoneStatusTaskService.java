package com.synaptix.taskmanager.example.tap.task.operation;

import com.synaptix.taskmanager.example.tap.model.OperationStatus;

public class DoneStatusTaskService extends AbstractOperationStatusTaskService {

    public DoneStatusTaskService() {
        super(OperationStatus.DONE);
    }

}
