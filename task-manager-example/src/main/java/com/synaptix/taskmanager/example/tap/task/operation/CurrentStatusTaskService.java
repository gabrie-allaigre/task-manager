package com.synaptix.taskmanager.example.tap.task.operation;

import com.synaptix.taskmanager.example.tap.model.OperationStatus;

public class CurrentStatusTaskService extends AbstractOperationStatusTaskService {

    public CurrentStatusTaskService() {
        super(OperationStatus.CURRENT);
    }

}
