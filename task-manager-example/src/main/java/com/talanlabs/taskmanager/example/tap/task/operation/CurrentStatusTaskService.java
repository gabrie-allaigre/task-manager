package com.talanlabs.taskmanager.example.tap.task.operation;

import com.talanlabs.taskmanager.example.tap.model.OperationStatus;

public class CurrentStatusTaskService extends AbstractOperationStatusTaskService {

    public CurrentStatusTaskService() {
        super(OperationStatus.CURRENT);
    }

}
