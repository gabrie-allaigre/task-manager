package com.synaptix.taskmanager.example.tap.task.operation;

import com.synaptix.taskmanager.engine.memory.SimpleStatusTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.tap.model.Operation;
import com.synaptix.taskmanager.example.tap.model.OperationStatus;

public class DoneStatusTaskService extends AbstractTaskService {

    public DoneStatusTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        SimpleStatusTask task = (SimpleStatusTask) commonTask;

        Operation operation = task.<Operation>getTaskObject();

        operation.setOperationStatus(OperationStatus.DONE);

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
