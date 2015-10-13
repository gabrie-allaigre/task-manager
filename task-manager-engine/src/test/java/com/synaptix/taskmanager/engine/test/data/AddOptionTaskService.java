package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class AddOptionTaskService extends AbstractTaskService {

    public AddOptionTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        OptionObject oo = new OptionObject();
        context.addTaskObjectsToTaskCluster(oo);

        ((SimpleSubTask) task).<BusinessObject>getTaskObject().setOptionObject(oo);
        return ExecutionResultBuilder.newBuilder().finished();
    }
}
