package com.talanlabs.taskmanager.example.tap.task.operation;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskservice.AbstractTaskService;
import com.talanlabs.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.talanlabs.taskmanager.example.tap.TapHelper;
import com.talanlabs.taskmanager.example.tap.model.Operation;
import com.talanlabs.taskmanager.jpa.JPATask;
import com.talanlabs.taskmanager.jpa.model.Task;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

public class WaitItemTaskService extends AbstractTaskService {

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        Task task = ((JPATask) commonTask).getTask();

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        Operation operation = em.find(Operation.class, task.getBusinessTaskObjectId());

        try {
            String value = BeanUtils.getProperty(operation.getFicheContact(), operation.getType());
            if (StringUtils.isNotBlank(value)) {
                em.getTransaction().begin();
                operation.setDoneFicheContactStatus(operation.getFicheContact().getFicheContactStatus());
                em.persist(operation);
                em.getTransaction().commit();
                return ExecutionResultBuilder.newBuilder().finished();
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return ExecutionResultBuilder.newBuilder().notFinished();
    }
}
