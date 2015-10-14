package com.synaptix.taskmanager.example.tap.task.operation;

import com.synaptix.taskmanager.engine.memory.SimpleSubTask;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.tap.model.Operation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;

public class WaitItemTaskService extends AbstractTaskService {

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        SimpleSubTask task = (SimpleSubTask) commonTask;

        Operation operation = task.<Operation>getTaskObject();

        try {
            String value = BeanUtils.getProperty(operation.getFicheContact(), operation.getType());
            if (StringUtils.isNotBlank(value)) {
                operation.setDoneFicheContactStatus(operation.getFicheContact().getFicheContactStatus());
                return ExecutionResultBuilder.newBuilder().finished();
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return ExecutionResultBuilder.newBuilder().notFinished();
    }
}
