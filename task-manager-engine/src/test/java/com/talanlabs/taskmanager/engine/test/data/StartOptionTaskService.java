package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.engine.memory.SimpleSubTask;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;

public class StartOptionTaskService extends AbstractTaskService {

    public StartOptionTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask task) {
        OptionObject oo = ((SimpleSubTask) task).<BusinessObject>getTaskObject().getOptionObject();
        if (oo == null) {
            oo = new OptionObject();
            ((SimpleSubTask) task).<BusinessObject>getTaskObject().setOptionObject(oo);
        }
        context.startEngine(oo);
        return ExecutionResultBuilder.newBuilder().finished();
    }
}
