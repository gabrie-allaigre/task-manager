package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.engine.memory.SimpleSubTask;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;

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
